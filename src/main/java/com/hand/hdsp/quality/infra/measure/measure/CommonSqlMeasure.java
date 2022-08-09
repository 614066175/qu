package com.hand.hdsp.quality.infra.measure.measure;

import com.alibaba.druid.DbType;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.domain.entity.ItemTemplateSql;
import com.hand.hdsp.quality.domain.repository.ItemTemplateSqlRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.measure.*;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.starter.driver.core.session.DriverSession;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hand.hdsp.quality.infra.constant.PlanConstant.CheckItem.*;

/**
 * <p>通用SQL处理</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-09 10:06:43
 */
@CheckItem(PlanConstant.COMMON_SQL)
public class CommonSqlMeasure implements Measure {

    private final ItemTemplateSqlRepository templateSqlRepository;
    private final CountCollector countCollector;
    private final DriverSessionService driverSessionService;

    private final List<String> checkItemList = Arrays.asList("FIELD_UNIQUE_LINE", "FIELD_EMPTY_LINE");

    public CommonSqlMeasure(ItemTemplateSqlRepository templateSqlRepository,
                            CountCollector countCollector, DriverSessionService driverSessionService) {
        this.templateSqlRepository = templateSqlRepository;
        this.countCollector = countCollector;
        this.driverSessionService = driverSessionService;
    }

    @Override
    public BatchResultItem check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        BatchResultBase batchResultBase = param.getBatchResultBase();
        BatchResultItem batchResultItem = param.getBatchResultItem();

        // 查询要执行的SQL
        ItemTemplateSql itemTemplateSql = templateSqlRepository.selectSql(ItemTemplateSql.builder()
                .checkItem(param.getCheckItem())
                .datasourceType(batchResultBase.getDatasourceType())
                .build());

        Map<String, String> variables = new HashMap<>(8);
        variables.put("table", batchResultBase.getPackageObjectName());
        variables.put("field", MeasureUtil.handleFieldName(param.getFieldName()));
        //维度字段
        variables.put("dimension", StringUtils.isEmpty(param.getDimensionField()) ? Strings.EMPTY : String.format("group by %s", MeasureUtil.handleFieldName(param.getDimensionField())));

        DriverSession driverSession = driverSessionService.getDriverSession(tenantId, param.getPluginDatasourceDTO().getDatasourceCode());
        List<Map<String, Object>> response = driverSession.executeOneQuery(param.getSchema(), MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition()));
        if (StringUtils.isEmpty(param.getDimensionField())) {
            //hive执行结果可能有执行日志
            if (!DbType.hive.equals(driverSession.getDbType())) {
                if (response.size() != 1 || response.get(0).size() != 1) {
                    throw new CommonException(ErrorCode.CHECK_ITEM_ONE_VALUE);
                }
            }

            String value = StringUtils.isEmpty(response.get(0).values().toArray()[0].toString()) ? "0" : response.get(0).values().toArray()[0].toString();
            param.setCountValue(value);
            //记录真实值，如果是非空/总行数，唯一数/总行数，需要额外记录另外两个数值（倍通项目提出需求 2022/08/09 15:20:19）
            if (checkItemList.contains(itemTemplateSql.getCheckItem())) {
                //进行处理
                String checkItem = itemTemplateSql.getCheckItem();
                checkItem = checkItem.substring(0, checkItem.lastIndexOf("_"));
                //非空数/唯一数查询
                ItemTemplateSql templateSql = templateSqlRepository.selectSql(ItemTemplateSql.builder()
                        .checkItem(checkItem)
                        .datasourceType(batchResultBase.getDatasourceType())
                        .build());
                List<Map<String, Object>> result = driverSession.executeOneQuery(param.getSchema(), MeasureUtil.replaceVariable(templateSql.getSqlContent(), variables, param.getWhereCondition()));
                Long resultNum = StringUtils.isEmpty(result.get(0).values().toArray()[0].toString()) ? 0 : Long.parseLong(result.get(0).values().toArray()[0].toString());
                if (FIELD_UNIQUE.equals(checkItem)) {
                    batchResultItem.setUniqueNum(resultNum);
                }
                if (FIELD_EMPTY.equals(checkItem)) {
                    batchResultItem.setNullNum(resultNum);
                }
                //表总数查询
                ItemTemplateSql tableLineSql = templateSqlRepository.selectSql(ItemTemplateSql.builder()
                        .checkItem(TABLE_LINE)
                        .datasourceType(batchResultBase.getDatasourceType())
                        .build());
                List<Map<String, Object>> tableLine = driverSession.executeOneQuery(param.getSchema(), MeasureUtil.replaceVariable(tableLineSql.getSqlContent(), variables, param.getWhereCondition()));
                Long tableLineNum = StringUtils.isEmpty(tableLine.get(0).values().toArray()[0].toString()) ? 0 : Long.parseLong(tableLine.get(0).values().toArray()[0].toString());
                batchResultItem.setTableLineNum(tableLineNum);
            }
            batchResultItem.setActualValue(value);
            batchResultItem.setCurrentValue(value);
            Count count = countCollector.getCount(param.getCountType());
            count.count(param);
        } else {
            //如果有维度字段，并且有多个结果则所有结果去进行比较
            if (response.size() != 1) {
                for (Map<String, Object> result : response) {
                    String value = result.values().toArray()[0].toString();
                    param.setCountValue(value);
                    Count count = countCollector.getCount(param.getCountType());
                    count.count(param);
                    //如果有异常，则记录并且将
                    if (param.getBatchResultItem() != null
                            && StringUtils.isNotEmpty(param.getBatchResultItem().getExceptionInfo())) {
                        batchResultItem.setActualValue(value);
                        batchResultItem.setCurrentValue(value);
                        break;
                    }
                }
            } else {
                //如果只有一个结果
                String value = response.get(0).values().toArray()[0].toString();
                param.setCountValue(value);
                batchResultItem.setActualValue(value);
                batchResultItem.setCurrentValue(value);
                Count count = countCollector.getCount(param.getCountType());
                count.count(param);
            }
        }

        return batchResultItem;
    }
}
