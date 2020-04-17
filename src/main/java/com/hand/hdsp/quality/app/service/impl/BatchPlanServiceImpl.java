package com.hand.hdsp.quality.app.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.BatchPlanService;
import com.hand.hdsp.quality.domain.entity.*;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.constant.TableNameConstant;
import com.hand.hdsp.quality.infra.converter.BatchPlanFieldLineConverter;
import com.hand.hdsp.quality.infra.converter.BatchPlanTableLineConverter;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import com.hand.hdsp.quality.infra.feign.DispatchJobFeign;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureCollector;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>批数据评估方案表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Slf4j
@Service
public class BatchPlanServiceImpl implements BatchPlanService {

    private static final String JOB_COMMAND = "curl -X GET --header 'Accept: */*' 'http://%s:%s/v2/%d/batch-plans/exec/%d'";
    private static final String TABLE_SQL = "SELECT table_rows,data_length FROM information_schema.TABLES WHERE table_schema='%s' AND table_name='%s'";
    @Value("${server.port}")
    private String port;

    @Autowired
    private BatchPlanBaseRepository batchPlanBaseRepository;
    @Autowired
    private BatchPlanRepository batchPlanRepository;
    @Autowired
    private BatchPlanTableRepository batchPlanTableRepository;
    @Autowired
    private BatchPlanTableLineRepository batchPlanTableLineRepository;
    @Autowired
    private BatchPlanFieldRepository batchPlanFieldRepository;
    @Autowired
    private BatchPlanFieldLineRepository batchPlanFieldLineRepository;
    @Autowired
    private BatchPlanRelTableRepository batchPlanRelTableRepository;
    @Autowired
    private BatchPlanRelTableLineRepository batchPlanRelTableLineRepository;
    @Autowired
    private PlanWarningLevelRepository planWarningLevelRepository;
    @Autowired
    private BatchResultRepository batchResultRepository;
    @Autowired
    private BatchResultBaseRepository batchResultBaseRepository;
    @Autowired
    private BatchResultRuleRepository batchResultRuleRepository;
    @Autowired
    private MeasureCollector measureCollector;
    @Autowired
    private BatchPlanTableLineConverter batchPlanTableLineConverter;
    @Autowired
    private BatchPlanFieldLineConverter batchPlanFieldLineConverter;
    @Autowired
    private DispatchJobFeign dispatchJobFeign;
    @Autowired
    private InetUtils inetUtils;
    @Autowired
    private DatasourceFeign datasourceFeign;
    @Autowired
    LovAdapter lovAdapter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(BatchPlanDTO batchPlanDTO) {
        List<BatchPlanBaseDTO> batchPlanBaseDTOList = batchPlanBaseRepository.selectDTO(BatchPlanBase.FIELD_PLAN_ID, batchPlanDTO.getPlanId());
        if (!batchPlanBaseDTOList.isEmpty()) {
            throw new CommonException(ErrorCode.CAN_NOT_DELETE);
        }
        return batchPlanRepository.deleteByPrimaryKey(batchPlanDTO);
    }


    @Override
    public void exec(Long tenantId, Long planId) {

        //插入批数据方案结果表
        BatchResult batchResult = BatchResult.builder()
                .planId(planId)
                .startDate(new Date())
                .planStatus(PlanConstant.PlanStatus.RUNNING)
                .tenantId(tenantId)
                .build();
        batchResultRepository.insertSelective(batchResult);

        try {
            //规则计算
            ruleJudge(tenantId, planId, batchResult);

            //分数计算
            countScore(tenantId, batchResult);
        } catch (Exception e) {
            log.error("count_error", e);
            batchResult.setPlanStatus(PlanConstant.PlanStatus.FAILED);
            batchResult.setExceptionInfo(e.getMessage());
            batchResultRepository.updateByPrimaryKeySelective(batchResult);
        }
    }

    @Override
    public void generate(Long tenantId, Long planId) {
        BatchPlanDTO batchPlanDTO = batchPlanRepository.selectDTOByPrimaryKey(planId);
        // 生成azkaban job的内容
        String ipAddress = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
        // 创建或更新job
        ResponseEntity<String> jobResult = dispatchJobFeign.createOrUpdate(tenantId,
                JobDTO.builder()
                        .themeId(PlanConstant.DEFAULT_THEME_ID)
                        .layerId(PlanConstant.DEFAULT_LAYER_ID)
                        .jobName("quality_" + batchPlanDTO.getPlanCode())
                        .jobDescription(batchPlanDTO.getPlanName())
                        .jobClass(PlanConstant.JOB_TYPE)
                        .jobType(PlanConstant.JOB_TYPE)
                        .jobCommand(String.format(JOB_COMMAND, ipAddress, port, tenantId, planId))
                        .jobLevel(PlanConstant.JOB_LEVEL)
                        .enabledFlag(1)
                        .tenantId(tenantId)
                        .build());
        ResponseUtils.getResponse(jobResult, JobDTO.class);
    }

    /**
     * 获取并设置表信息
     *
     * @param datasourceDTO   datasourceDTO
     * @param batchResultBase batchResultBase
     */
    private void setTableInfo(DatasourceDTO datasourceDTO, BatchResultBase batchResultBase) {
        datasourceDTO.setSql(String.format(TABLE_SQL, datasourceDTO.getSchema(), datasourceDTO.getTableName()));
        List<Map<String, Long>> result = ResponseUtils.getResponse(datasourceFeign.execSql(datasourceDTO.getTenantId(), datasourceDTO), new TypeReference<List<Map<String, Long>>>() {
        });
        if (!result.isEmpty()) {
            batchResultBase.setDataCount(result.get(0).get("table_rows"));
            batchResultBase.setTableSize(result.get(0).get("data_length"));
        } else {
            throw new CommonException("查询表行数和大小失败，请联系系统管理员！");
        }
    }

    /**
     * 对每个规则进行计算
     *
     * @param tenantId    tenantId
     * @param planId      planId
     * @param batchResult batchResult
     */
    private void ruleJudge(Long tenantId, Long planId, BatchResult batchResult) {

        List<BatchPlanBase> baseList = batchPlanBaseRepository.select(BatchPlanBase.FIELD_PLAN_ID, planId);
        for (BatchPlanBase batchPlanBase : baseList) {

            // 构建DatasourceDTO
            DatasourceDTO datasourceDTO = DatasourceDTO.builder()
                    .datasourceId(batchPlanBase.getDatasourceId())
                    .schema(batchPlanBase.getDatasourceSchema())
                    .tableName(batchPlanBase.getTableName())
                    .tenantId(tenantId)
                    .build();

            //插入批数据方案结果表-表信息
            BatchResultBase batchResultBase = BatchResultBase.builder()
                    .resultId(batchResult.getResultId())
                    .planBaseId(batchPlanBase.getPlanBaseId())
                    .tableName(batchPlanBase.getTableName())
                    .ruleCount(0L)
                    .exceptionRuleCount(0L)
                    .checkItemCount(0L)
                    .exceptionCheckItemCount(0L)
                    .tenantId(tenantId)
                    .build();
            // 查询并设置表行数和表大小
            setTableInfo(datasourceDTO, batchResultBase);
            batchResultBaseRepository.insertSelective(batchResultBase);

            handleTableRule(tenantId, batchResultBase, datasourceDTO);
            handleFieldRule(tenantId, batchResultBase, datasourceDTO);
            handleRelTableRule(tenantId, batchResultBase, datasourceDTO);

            batchResultBaseRepository.updateByPrimaryKeySelective(batchResultBase);
        }
    }

    /**
     * 处理表级规则
     *
     * @param tenantId        tenantId
     * @param batchResultBase batchResultBase
     * @param datasourceDTO   datasourceDTO
     */
    private void handleTableRule(Long tenantId, BatchResultBase batchResultBase, DatasourceDTO datasourceDTO) {
        List<BatchPlanTable> tableList = batchPlanTableRepository.select(BatchPlanTable.FIELD_PLAN_BASE_ID, batchResultBase.getPlanBaseId());
        batchResultBase.setRuleCount(batchResultBase.getRuleCount() + tableList.size());

        for (BatchPlanTable batchPlanTable : tableList) {
            List<BatchPlanTableLine> lineList = batchPlanTableLineRepository.select(BatchPlanTableLine.FIELD_PLAN_TABLE_ID, batchPlanTable.getPlanTableId());
            batchResultBase.setCheckItemCount(batchResultBase.getCheckItemCount() + lineList.size());

            //异常标记
            boolean exceptionFlag = false;

            for (BatchPlanTableLine batchPlanTableLine : lineList) {
                List<PlanWarningLevel> warningLevelList = planWarningLevelRepository.select(
                        PlanWarningLevel.builder()
                                .sourceId(batchPlanTableLine.getPlanTableLineId())
                                .sourceType(TableNameConstant.XQUA_BATCH_PLAN_TABLE_LINE)
                                .build());

                BatchPlanTableLineDTO batchPlanTableLineDTO = batchPlanTableLineConverter.entityToDto(batchPlanTableLine);
                Measure measure = measureCollector.getMeasure(batchPlanTableLine.getCheckItem().toUpperCase());

                BatchResultRuleDTO batchResultRuleDTO = measure.check(MeasureParamDO.builder()
                        .tenantId(tenantId)
                        .datasourceDTO(datasourceDTO)
                        .batchPlanTableLineDTO(batchPlanTableLineDTO)
                        .warningLevelList(warningLevelList)
                        .batchResultBase(batchResultBase)
                        .build());
                batchResultRuleDTO.setResultBaseId(batchResultBase.getResultBaseId());
                batchResultRuleDTO.setRuleType(PlanConstant.ResultRuleType.TABLE);
                batchResultRuleDTO.setTableName(batchResultBase.getTableName());
                batchResultRuleDTO.setRuleId(batchPlanTable.getPlanTableId());
                batchResultRuleDTO.setRuleCode(batchPlanTable.getRuleCode());
                batchResultRuleDTO.setRuleName(batchPlanTable.getRuleName());
                batchResultRuleDTO.setCheckItem(batchPlanTableLine.getCheckItem());
                batchResultRuleRepository.insertDTOSelective(batchResultRuleDTO);

                if (StringUtils.isNotBlank(batchResultRuleDTO.getWarningLevel())) {
                    batchResultBase.setExceptionCheckItemCount(batchResultBase.getExceptionCheckItemCount() + 1);
                    exceptionFlag = true;
                }
            }

            if (exceptionFlag) {
                batchResultBase.setExceptionRuleCount(batchResultBase.getExceptionRuleCount() + 1);
            }
        }
    }

    /**
     * 处理字段规则
     *
     * @param tenantId        tenantId
     * @param batchResultBase batchResultBase
     * @param datasourceDTO   datasourceDTO
     */
    private void handleFieldRule(Long tenantId, BatchResultBase batchResultBase, DatasourceDTO datasourceDTO) {
        List<BatchPlanField> fieldList = batchPlanFieldRepository.select(BatchPlanTable.FIELD_PLAN_BASE_ID, batchResultBase.getPlanBaseId());
        batchResultBase.setRuleCount(batchResultBase.getRuleCount() + fieldList.size());

        for (BatchPlanField batchPlanField : fieldList) {
            List<BatchPlanFieldLine> lineList = batchPlanFieldLineRepository.select(BatchPlanFieldLine.FIELD_PLAN_FIELD_ID, batchPlanField.getPlanFieldId());
            batchResultBase.setCheckItemCount(batchResultBase.getCheckItemCount() + lineList.size());

            //异常标记
            boolean exceptionFlag = false;

            for (BatchPlanFieldLine batchPlanFieldLine : lineList) {
                List<PlanWarningLevel> warningLevelList = planWarningLevelRepository.select(
                        PlanWarningLevel.builder()
                                .sourceId(batchPlanFieldLine.getPlanFieldLineId())
                                .sourceType(TableNameConstant.XQUA_BATCH_PLAN_FIELD_LINE)
                                .build());

                BatchPlanFieldLineDTO batchPlanFieldLineDTO = batchPlanFieldLineConverter.entityToDto(batchPlanFieldLine);
                Measure measure = measureCollector.getMeasure(batchPlanFieldLine.getCheckItem().toUpperCase());

                BatchResultRuleDTO batchResultRuleDTO = measure.check(MeasureParamDO.builder()
                        .tenantId(tenantId)
                        .datasourceDTO(datasourceDTO)
                        .batchPlanField(batchPlanField)
                        .batchPlanFieldLineDTO(batchPlanFieldLineDTO)
                        .warningLevelList(warningLevelList)
                        .batchResultBase(batchResultBase)
                        .build());
                batchResultRuleDTO.setResultBaseId(batchResultBase.getResultBaseId());
                batchResultRuleDTO.setRuleType(PlanConstant.ResultRuleType.FIELD);
                batchResultRuleDTO.setTableName(batchResultBase.getTableName());
                batchResultRuleDTO.setRuleId(batchPlanField.getPlanFieldId());
                batchResultRuleDTO.setPlanFieldLineId(batchPlanFieldLine.getPlanFieldLineId());
                batchResultRuleDTO.setRuleCode(batchPlanField.getRuleCode());
                batchResultRuleDTO.setRuleName(batchPlanField.getRuleName());
                batchResultRuleDTO.setFieldName(batchPlanField.getFieldName());
                batchResultRuleDTO.setCheckItem(batchPlanFieldLine.getCheckItem());
                batchResultRuleRepository.insertDTOSelective(batchResultRuleDTO);

                if (StringUtils.isNotBlank(batchResultRuleDTO.getWarningLevel())) {
                    batchResultBase.setExceptionCheckItemCount(batchResultBase.getExceptionCheckItemCount() + 1);
                    exceptionFlag = true;
                }
            }

            if (exceptionFlag) {
                batchResultBase.setExceptionRuleCount(batchResultBase.getExceptionRuleCount() + 1);
            }
        }
    }

    /**
     * 处理表间规则
     *
     * @param tenantId        tenantId
     * @param batchResultBase batchResultBase
     * @param datasourceDTO   datasourceDTO
     */
    private void handleRelTableRule(Long tenantId, BatchResultBase batchResultBase, DatasourceDTO datasourceDTO) {

        List<BatchPlanRelTable> relTableList = batchPlanRelTableRepository.select(BatchPlanTable.FIELD_PLAN_BASE_ID, batchResultBase.getPlanBaseId());
        batchResultBase.setRuleCount(batchResultBase.getRuleCount() + relTableList.size());
        batchResultBase.setCheckItemCount(batchResultBase.getCheckItemCount() + relTableList.size());

        for (BatchPlanRelTable batchPlanRelTable : relTableList) {
            List<PlanWarningLevel> warningLevelList = planWarningLevelRepository.select(
                    PlanWarningLevel.builder()
                            .sourceId(batchPlanRelTable.getPlanRelTableId())
                            .sourceType(TableNameConstant.XQUA_BATCH_PLAN_REL_TABLE)
                            .build());

            List<BatchPlanRelTableLine> lineList = batchPlanRelTableLineRepository.select(BatchPlanRelTableLine.FIELD_PLAN_REL_TABLE_ID, batchPlanRelTable.getPlanRelTableId());

            Measure measure = measureCollector.getMeasure(PlanConstant.RuleType.TABLE_RELATION);

            BatchResultRuleDTO batchResultRuleDTO = measure.check(MeasureParamDO.builder()
                    .tenantId(tenantId)
                    .datasourceDTO(datasourceDTO)
                    .batchPlanRelTable(batchPlanRelTable)
                    .batchPlanRelTableLineList(lineList)
                    .warningLevelList(warningLevelList)
                    .batchResultBase(batchResultBase)
                    .build());

            batchResultRuleDTO.setResultBaseId(batchResultBase.getResultBaseId());
            batchResultRuleDTO.setRuleType(PlanConstant.ResultRuleType.REL_TABLE);
            batchResultRuleDTO.setTableName(batchResultBase.getTableName());
            batchResultRuleDTO.setRuleId(batchPlanRelTable.getPlanRelTableId());
            batchResultRuleDTO.setRuleCode(batchPlanRelTable.getRuleCode());
            batchResultRuleDTO.setRuleName(batchPlanRelTable.getRuleName());
            batchResultRuleRepository.insertDTOSelective(batchResultRuleDTO);

            if (StringUtils.isNotBlank(batchResultRuleDTO.getWarningLevel())) {
                batchResultBase.setExceptionRuleCount(batchResultBase.getExceptionRuleCount() + 1);
                batchResultBase.setExceptionCheckItemCount(batchResultBase.getExceptionCheckItemCount() + 1);
            }
        }
    }

    /**
     * 分数计算
     *
     * @param tenantId    tenantId
     * @param batchResult 结果表对象
     */
    private void countScore(Long tenantId, BatchResult batchResult) {

        //获取所有告警等级，计算 F 值
        List<LovValueDTO> list = lovAdapter.queryLovValue(PlanConstant.LOV_WARNING_LEVEL, tenantId);
        Map<String, BigDecimal> map = new HashMap<>();
        BigDecimal n = BigDecimal.valueOf(list.size());
        BigDecimal half = BigDecimal.valueOf(0.5);
        BigDecimal f = BigDecimal.ONE.divide(n, 10, RoundingMode.HALF_UP).multiply(half);
        for (LovValueDTO lovValueDTO : list) {
            map.put(lovValueDTO.getValue(), f);
            f = f.add(BigDecimal.ONE.divide(n, 10, RoundingMode.HALF_UP));
        }
        //正常规则F 为1
        map.put(PlanConstant.WARNING_LEVEL_NORMAL, BigDecimal.ONE);

        // 查询规则
        List<BatchResultRuleDTO> resultRuleDTOList = batchResultRuleRepository.selectByResultId(batchResult.getResultId());
        BigDecimal w = BigDecimal.valueOf(resultRuleDTOList.stream().mapToLong(BatchResultRuleDTO::getWeight).sum());
        BigDecimal sum = BigDecimal.ZERO;
        for (BatchResultRuleDTO batchResultRuleDTO : resultRuleDTOList) {
            BigDecimal multiply = BigDecimal.valueOf(batchResultRuleDTO.getWeight())
                    .divide(w, 10, RoundingMode.HALF_UP)
                    .multiply(map.get(batchResultRuleDTO.getWarningLevel()));
            sum = sum.add(multiply);
        }
        batchResult.setMark(sum.multiply(BigDecimal.valueOf(100)));
        batchResult.setPlanStatus(PlanConstant.PlanStatus.SUCCESS);
        batchResult.setEndDate(new Date());
        batchResultRepository.updateByPrimaryKey(batchResult);
    }

}
