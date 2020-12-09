package com.hand.hdsp.quality.infra.measure.measure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hand.hdsp.quality.api.dto.WarningLevelDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.domain.entity.ItemTemplateSql;
import com.hand.hdsp.quality.domain.repository.ItemTemplateSqlRepository;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.dataobject.MeasureResultDO;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.starter.driver.core.session.DriverSession;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/07 20:55
 * @since 1.0
 */
@CheckItem(PlanConstant.CheckItem.DATA_LENGTH)
public class DataLengthMeasure implements Measure {

    public static final String COUNT = "COUNT";
    private final ItemTemplateSqlRepository templateSqlRepository;
    private final DriverSessionService driverSessionService;

    public DataLengthMeasure(ItemTemplateSqlRepository templateSqlRepository, DriverSessionService driverSessionService) {
        this.templateSqlRepository = templateSqlRepository;
        this.driverSessionService = driverSessionService;
    }


    @Override
    public BatchResultItem check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        BatchResultBase batchResultBase = param.getBatchResultBase();
        BatchResultItem batchResultItem = param.getBatchResultItem();
        List<WarningLevelDTO> warningLevelList = param.getWarningLevelList();
        DriverSession driverSession = driverSessionService.getDriverSession(tenantId, param.getPluginDatasourceDTO().getDatasourceCode());
        // 值集校验
        String countType = param.getCountType();
        //固定值
        if (PlanConstant.CountType.FIXED_VALUE.equals(countType)) {
            // 查询要执行的SQL
            ItemTemplateSql itemTemplateSql = templateSqlRepository.selectSql(ItemTemplateSql.builder()
                    .checkItem(param.getCountType())
                    .datasourceType(batchResultBase.getDatasourceType())
                    .build());

            Map<String, String> variables = new HashMap<>(8);
            variables.put("table", batchResultBase.getPackageObjectName());
            variables.put("field", MeasureUtil.handleFieldName(param.getFieldName()));
            variables.put("size", PlanConstant.DEFAULT_SIZE + "");

            boolean successFlag = true;
            for (int i = 0; ; i++) {
                int start = i * PlanConstant.DEFAULT_SIZE;
                variables.put("start", start + "");
                List<Map<String, Object>> maps = driverSession.executeOneQuery(param.getSchema(), MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition()));
                List<MeasureResultDO> list = new ArrayList<>();
                maps.forEach((map) -> map.forEach((k, v) -> list.add(new MeasureResultDO(String.valueOf(v)))));
                for (MeasureResultDO measureResultDO : list) {
                    MeasureUtil.fixedCompare(param.getCompareWay(), measureResultDO.getResult(), warningLevelList, batchResultItem);
                    if (StringUtils.isNotBlank(batchResultItem.getWarningLevel())) {
                        batchResultItem.setActualValue(measureResultDO.getResult());
                        successFlag = false;
                        break;
                    }
                }

                //当成功标记为false 或者 查询出来的数据量小于每页大小时（即已到最后一页了）退出
                if (!successFlag || list.size() < PlanConstant.DEFAULT_SIZE) {
                    break;
                }
            }
        }
        //长度范围
        else if (PlanConstant.CountType.LENGTH_RANGE.equals(countType)) {
            // 查询要执行的SQL
            ItemTemplateSql itemTemplateSql = templateSqlRepository.selectSql(ItemTemplateSql.builder()
                    .checkItem(param.getCountType())
                    .datasourceType(batchResultBase.getDatasourceType())
                    .build());
            long count;
            long start = 0L;
            long end = 0L;
            if (CollectionUtils.isNotEmpty(warningLevelList) && warningLevelList.size() == 2) {
                start = Long.parseLong(warningLevelList.get(0).getEndValue()) + 1;
                end = Long.parseLong(warningLevelList.get(1).getStartValue()) - 1;
            }

            Map<String, String> variables = new HashMap<>(8);
            variables.put("table", batchResultBase.getPackageObjectName());
            variables.put("field", MeasureUtil.handleFieldName(param.getFieldName()));
            variables.put("start", Long.toString(start));
            variables.put("end", Long.toString(end));
            List<Map<String, Object>> maps = driverSession.executeOneQuery(param.getSchema(),
                    MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition()));
            if (CollectionUtils.isNotEmpty(maps)) {
                count= (long) maps.get(0).get(COUNT);
                batchResultItem.setWarningLevel(warningLevelList.get(0).getWarningLevel());
                batchResultItem.setExceptionInfo(String.format("存在%d条数据不满足(%s,%s)范围",count,start,end));
            }
        }
        return batchResultItem;
    }
}
