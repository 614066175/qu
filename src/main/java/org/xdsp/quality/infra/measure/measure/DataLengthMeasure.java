package org.xdsp.quality.infra.measure.measure;

import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.starter.driver.core.infra.util.JsonUtil;
import org.hzero.starter.driver.core.session.DriverSession;
import org.xdsp.quality.api.dto.WarningLevelDTO;
import org.xdsp.quality.domain.entity.BatchResultBase;
import org.xdsp.quality.domain.entity.BatchResultItem;
import org.xdsp.quality.domain.entity.ItemTemplateSql;
import org.xdsp.quality.domain.repository.ItemTemplateSqlRepository;
import org.xdsp.quality.infra.constant.PlanConstant;
import org.xdsp.quality.infra.dataobject.MeasureParamDO;
import org.xdsp.quality.infra.measure.CheckItem;
import org.xdsp.quality.infra.measure.Measure;
import org.xdsp.quality.infra.measure.MeasureUtil;
import org.xdsp.quality.infra.util.PlanExceptionUtil;
import org.xdsp.quality.infra.vo.WarningLevelVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

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


    private final ItemTemplateSqlRepository templateSqlRepository;
    private final DriverSessionService driverSessionService;
    private final LovAdapter lovAdapter;
    public static final String COUNT = "COUNT";
    public static final String DATA_LENGTH_VALUE_RANGE = "DATA_LENGTH_VALUE_RANGE";
//    public static final String START_VALUE = " and length(${field}) >= %s";
//    public static final String END_VALUE = " and length(${field}) <= %s";

    public DataLengthMeasure(ItemTemplateSqlRepository templateSqlRepository, DriverSessionService driverSessionService, LovAdapter lovAdapter) {
        this.templateSqlRepository = templateSqlRepository;
        this.driverSessionService = driverSessionService;
        this.lovAdapter = lovAdapter;
    }


    @Override
    public BatchResultItem check(MeasureParamDO param) {
        Long tenantId = param.getTenantId();
        BatchResultBase batchResultBase = param.getBatchResultBase();
        BatchResultItem batchResultItem = param.getBatchResultItem();
        List<WarningLevelDTO> warningLevelList = param.getWarningLevelList();
        DriverSession driverSession = driverSessionService.getDriverSession(tenantId, param.getPluginDatasourceDTO().getDatasourceCode());
        //固定值
        if (PlanConstant.CompareWay.VALUE.equals(param.getCompareWay())) {
            List<WarningLevelVO> warningLevelVOList = new ArrayList<>();
            //根据告警配置来执行多个sql获取不同告警配置的结果
            warningLevelList.forEach(warningLevelDTO -> {
                // 查询要执行的SQL
                ItemTemplateSql itemTemplateSql = templateSqlRepository.selectSql(ItemTemplateSql.builder()
                        .checkItem(String.format("%s_%s", param.getCheckItem(), param.getCompareWay()))
                        .datasourceType(batchResultBase.getDatasourceType())
                        .build());

                Map<String, String> variables = new HashMap<>(8);
                variables.put("table", batchResultBase.getPackageObjectName());
                variables.put("field", MeasureUtil.handleFieldName(param.getFieldName()));
                variables.put("compareSymbol", PlanConstant.CompareSymbol.EQUAL.equals(warningLevelDTO.getCompareSymbol()) ? "=" : "!=");
                variables.put("num", warningLevelDTO.getExpectedValue());
                String sql = MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition());
                List<Map<String, Object>> maps = driverSession.executeOneQuery(param.getSchema(), sql);
                if (Integer.parseInt(maps.get(0).values().toArray()[0].toString()) != 0) {
                    warningLevelVOList.add(
                            WarningLevelVO.builder()
                                    .warningLevel(warningLevelDTO.getWarningLevel())
                                    .levelCount(Long.parseLong(maps.get(0).values().toArray()[0].toString()))
                                    .build());
                    PlanExceptionUtil.getPlanException(param, batchResultBase, sql, driverSession, warningLevelDTO);
                }
            });
            AtomicLong count = new AtomicLong();
            warningLevelVOList.forEach(warningLevelVO -> count.set(count.get() + warningLevelVO.getLevelCount()));
            batchResultItem.setExceptionInfo(String.format("存在%d条数据满足告警要求", count.get()));
            batchResultItem.setWarningLevel(JsonUtil.toJson(warningLevelVOList));
        }
        //长度范围
        else if (PlanConstant.CompareWay.RANGE.equals(param.getCompareWay())) {
            List<WarningLevelVO> warningLevelVOList = new ArrayList<>();
            //根据告警配置来执行多个sql获取不同告警配置的结果
            warningLevelList.forEach(warningLevelDTO -> {
                // 查询要执行的SQL
                ItemTemplateSql itemTemplateSql = templateSqlRepository.selectSql(ItemTemplateSql.builder()
                        .checkItem(String.format("%s_%s", param.getCheckItem(), param.getCompareWay()))
                        .datasourceType(batchResultBase.getDatasourceType())
                        .build());
                ItemTemplateSql dataLengthValue = templateSqlRepository.selectSql(ItemTemplateSql.builder()
                        .checkItem(DATA_LENGTH_VALUE_RANGE)
                        .datasourceType(batchResultBase.getDatasourceType())
                        .build());
                StringBuilder condition = new StringBuilder();
                if (Strings.isNotEmpty(warningLevelDTO.getStartValue())) {
                    condition.append(String.format(dataLengthValue.getSqlContent(), ">=", warningLevelDTO.getStartValue()));
                }
                if (Strings.isNotEmpty(warningLevelDTO.getEndValue())) {
                    condition.append(String.format(dataLengthValue.getSqlContent(), "<=", warningLevelDTO.getEndValue()));
                }
                itemTemplateSql.setSqlContent(itemTemplateSql.getSqlContent() + condition);
                Map<String, String> variables = new HashMap<>(8);
                variables.put("table", batchResultBase.getPackageObjectName());
                variables.put("field", MeasureUtil.handleFieldName(param.getFieldName()));
                String sql = MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition());
                List<Map<String, Object>> maps = driverSession.executeOneQuery(param.getSchema(), sql);
                if (Integer.parseInt(maps.get(0).values().toArray()[0].toString()) != 0) {
                    warningLevelVOList.add(
                            WarningLevelVO.builder()
                                    .warningLevel(warningLevelDTO.getWarningLevel())
                                    .levelCount(Long.parseLong(maps.get(0).values().toArray()[0].toString()))
                                    .build());
                    //有异常，查询异常数据
                    PlanExceptionUtil.getPlanException(param, batchResultBase, sql, driverSession, warningLevelDTO);
                }
            });
            AtomicLong count = new AtomicLong();
            warningLevelVOList.forEach(warningLevelVO -> count.set(count.get() + warningLevelVO.getLevelCount()));
            batchResultItem.setExceptionInfo(String.format("存在%d条数据满足告警要求", count.get()));
            batchResultItem.setWarningLevel(JsonUtil.toJson(warningLevelVOList));
        }
        return batchResultItem;
    }
}
