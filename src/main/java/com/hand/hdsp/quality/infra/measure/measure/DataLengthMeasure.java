package com.hand.hdsp.quality.infra.measure.measure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.hand.hdsp.quality.api.dto.WarningLevelDTO;
import com.hand.hdsp.quality.domain.entity.BatchResultBase;
import com.hand.hdsp.quality.domain.entity.BatchResultItem;
import com.hand.hdsp.quality.domain.entity.ItemTemplateSql;
import com.hand.hdsp.quality.domain.repository.ItemTemplateSqlRepository;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.measure.CheckItem;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureUtil;
import com.hand.hdsp.quality.infra.util.PlanExceptionUtil;
import com.hand.hdsp.quality.infra.vo.WarningLevelVO;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.starter.driver.core.infra.util.JsonUtil;
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


    private final ItemTemplateSqlRepository templateSqlRepository;
    private final DriverSessionService driverSessionService;
    private final LovAdapter lovAdapter;
    public static final String COUNT = "COUNT";
    public static final String START_VALUE = " and length(${field}) > %s";
    public static final String END_VALUE = " and length(${field}) < %s";

    public DataLengthMeasure(ItemTemplateSqlRepository templateSqlRepository, DriverSessionService driverSessionService, LovAdapter lovAdapter) {
        this.templateSqlRepository = templateSqlRepository;
        this.driverSessionService = driverSessionService;
        this.lovAdapter = lovAdapter;
    }


    @Override
    public BatchResultItem check(MeasureParamDO param) {
        //是否需要按告警等级排序
//        List<LovValueDTO> lovList = lovAdapter.queryLovValue(PlanConstant.LOV_WARNING_LEVEL, param.getTenantId());
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
                        .checkItem(String.format("%s_%s",param.getCheckItem(),param.getCompareWay()))
                        .datasourceType(batchResultBase.getDatasourceType())
                        .build());

                Map<String, String> variables = new HashMap<>(8);
                variables.put("table", batchResultBase.getPackageObjectName());
                variables.put("field", MeasureUtil.handleFieldName(param.getFieldName()));
                variables.put("num",warningLevelDTO.getExpectedValue());
                String sql = MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition());
                List<Map<String, Object>> maps = driverSession.executeOneQuery(param.getSchema(),sql);
                if (Integer.parseInt(String.valueOf(maps.get(0).get(COUNT))) != 0) {
                    warningLevelVOList.add(
                            WarningLevelVO.builder()
                                    .warningLevel(warningLevelDTO.getWarningLevel())
                                    .levelCount((Long) maps.get(0).get(COUNT))
                                    .build());
                    PlanExceptionUtil.getPlanException(param,batchResultBase.getPackageObjectName(),sql,driverSession, warningLevelDTO);
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
                StringBuilder condition = new StringBuilder();
                if (Strings.isNotEmpty(warningLevelDTO.getStartValue())) {
                    condition.append(String.format(START_VALUE, warningLevelDTO.getStartValue()));
                }
                if (Strings.isNotEmpty(warningLevelDTO.getEndValue())) {
                    condition.append(String.format(END_VALUE, warningLevelDTO.getEndValue()));
                }
                itemTemplateSql.setSqlContent(itemTemplateSql.getSqlContent() + condition);
                Map<String, String> variables = new HashMap<>(8);
                variables.put("table", batchResultBase.getPackageObjectName());
                variables.put("field", MeasureUtil.handleFieldName(param.getFieldName()));
                String sql = MeasureUtil.replaceVariable(itemTemplateSql.getSqlContent(), variables, param.getWhereCondition());
                List<Map<String, Object>> maps = driverSession.executeOneQuery(param.getSchema(),sql);
                if (Integer.parseInt(String.valueOf(maps.get(0).get(COUNT))) != 0) {
                    warningLevelVOList.add(
                            WarningLevelVO.builder()
                                    .warningLevel(warningLevelDTO.getWarningLevel())
                                    .levelCount((Long) maps.get(0).get(COUNT))
                                    .build());
                    //有异常，查询异常数据
                    PlanExceptionUtil.getPlanException(param,batchResultBase.getPackageObjectName(),sql,driverSession, warningLevelDTO);
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
