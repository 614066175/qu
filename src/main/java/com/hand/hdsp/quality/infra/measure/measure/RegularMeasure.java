package com.hand.hdsp.quality.infra.measure.measure;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
import com.hand.hdsp.quality.infra.util.JsonUtils;
import com.hand.hdsp.quality.infra.vo.WarningLevelVO;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.starter.driver.core.session.DriverSession;

/**
 * <p>正则表达式</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@CheckItem(PlanConstant.CheckWay.REGULAR)
public class RegularMeasure implements Measure {

    private final ItemTemplateSqlRepository templateSqlRepository;
    private final DriverSessionService driverSessionService;

    public RegularMeasure(ItemTemplateSqlRepository templateSqlRepository, DriverSessionService driverSessionService) {
        this.templateSqlRepository = templateSqlRepository;
        this.driverSessionService = driverSessionService;
    }

    @Override
    public BatchResultItem check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        BatchResultBase batchResultBase = param.getBatchResultBase();
        BatchResultItem batchResultItem = param.getBatchResultItem();
        DriverSession driverSession = driverSessionService.getDriverSession(tenantId, param.getPluginDatasourceDTO().getDatasourceCode());

        // 查询要执行的SQL
        ItemTemplateSql itemTemplateSql = templateSqlRepository.selectSql(ItemTemplateSql.builder()
                .checkItem(PlanConstant.CheckWay.REGULAR)
                .datasourceType(batchResultBase.getDatasourceType())
                .build());

        // 用执行SQL的方式校验
        if (PlanConstant.TemplateSqlTag.SQL.equals(itemTemplateSql.getTag())) {
            Map<String, String> variables = new HashMap<>(8);
            variables.put("table", batchResultBase.getPackageObjectName());
            variables.put("field", MeasureUtil.handleFieldName(param.getFieldName()));
            variables.put("regexp", param.getRegularExpression());


            List<Map<String, Object>> response = driverSession.executeOneQuery(param.getSchema(),
                    MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition()));

            String value = response.get(0).values().toArray(new String[0])[0];
            BigDecimal result = new BigDecimal(value);
            if (result.compareTo(BigDecimal.ONE) != 0) {
                batchResultItem.setWarningLevel(JsonUtils.object2Json(WarningLevelVO.builder()
                        .warningLevel(param.getWarningLevelList().get(0).getWarningLevel())
                        .build()));
                batchResultItem.setExceptionInfo("不满足正则表达式");
            }
        }
        // 查询出数据在Java里校验
        else if (PlanConstant.TemplateSqlTag.JAVA.equals(itemTemplateSql.getTag())) {
            Map<String, String> variables = new HashMap<>(8);
            variables.put("table", batchResultBase.getPackageObjectName());
            variables.put("field", MeasureUtil.handleFieldName(param.getFieldName()));
            variables.put("size", PlanConstant.DEFAULT_SIZE + "");

            Pattern pattern = Pattern.compile(param.getRegularExpression());

            boolean successFlag = true;
            for (int i = 0; ; i++) {
                int start = i * PlanConstant.DEFAULT_SIZE;
                variables.put("start", start + "");
                List<Map<String, Object>> maps = driverSession.executeOneQuery(param.getSchema(),
                        MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition()));
                List<MeasureResultDO> list = new ArrayList<>();
                maps.forEach((map) -> map.forEach((k, v) -> list.add(new MeasureResultDO(String.valueOf(v)))));
                for (MeasureResultDO measureResultDO : list) {
                    if (!pattern.matcher(measureResultDO.getResult()).find()) {
                        batchResultItem.setActualValue(measureResultDO.getResult());
                        batchResultItem.setWarningLevel(JsonUtils.object2Json(WarningLevelVO.builder()
                                .warningLevel(param.getWarningLevelList().get(0).getWarningLevel())
                                .build()));
                        batchResultItem.setExceptionInfo("不满足正则表达式");
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

        return batchResultItem;
    }
}
