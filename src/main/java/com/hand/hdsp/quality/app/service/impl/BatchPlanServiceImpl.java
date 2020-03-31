package com.hand.hdsp.quality.app.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.BatchPlanService;
import com.hand.hdsp.quality.domain.entity.*;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.converter.BatchPlanFieldLineConverter;
import com.hand.hdsp.quality.infra.converter.BatchPlanTableLineConverter;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import com.hand.hdsp.quality.infra.feign.DispatchJobFeign;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureCollector;
import io.choerodon.core.exception.CommonException;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

        List<BatchPlanBase> baseList = batchPlanBaseRepository.select(BatchPlanBase.FIELD_PLAN_ID, planId);
        for (BatchPlanBase batchPlanBase : baseList) {

            // 构建DatasourceDTO
            DatasourceDTO datasourceDTO = DatasourceDTO.builder()
                    .datasourceId(batchPlanBase.getDatasourceId())
                    .schema(batchPlanBase.getDatasourceSchema())
                    .tableName(batchPlanBase.getTableName())
                    .tenantId(tenantId)
                    .build();

            List<BatchPlanTable> tableList = batchPlanTableRepository.select(BatchPlanTable.FIELD_PLAN_BASE_ID, batchPlanBase.getPlanBaseId());
            List<BatchPlanField> fieldList = batchPlanFieldRepository.select(BatchPlanTable.FIELD_PLAN_BASE_ID, batchPlanBase.getPlanBaseId());
            List<BatchPlanRelTable> relTableList = batchPlanRelTableRepository.select(BatchPlanTable.FIELD_PLAN_BASE_ID, batchPlanBase.getPlanBaseId());


            //插入批数据方案结果表-表信息
            BatchResultBase batchResultBase = BatchResultBase.builder()
                    .resultId(batchResult.getResultId())
                    .planBaseId(batchPlanBase.getPlanBaseId())
                    .tableName(batchPlanBase.getTableName())
                    .ruleCount((long) tableList.size() + fieldList.size() + relTableList.size())
                    .tenantId(tenantId)
                    .build();
            // 查询并设置表行数和表大小
            setTableInfo(datasourceDTO, batchResultBase);
            batchResultBaseRepository.insertSelective(batchResultBase);

            for (BatchPlanTable batchPlanTable : tableList) {
                List<BatchPlanTableLine> lineList = batchPlanTableLineRepository.select(BatchPlanTableLine.FIELD_PLAN_TABLE_ID, batchPlanTable.getPlanTableId());
                for (BatchPlanTableLine batchPlanTableLine : lineList) {
                    List<PlanWarningLevel> warningLevelList = planWarningLevelRepository.select(
                            PlanWarningLevel.builder()
                                    .sourceId(batchPlanTableLine.getPlanTableLineId())
                                    .sourceType(PlanConstant.WARNING_LEVEL_TABLE)
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
                    batchResultRuleDTO.setRuleType("TABLE");
                    batchResultRuleDTO.setTableName(batchPlanBase.getTableName());
                    batchResultRuleDTO.setRuleId(batchPlanTable.getPlanTableId());
                    batchResultRuleDTO.setRuleCode(batchPlanTable.getRuleCode());
                    batchResultRuleDTO.setRuleName(batchPlanTable.getRuleName());
                    batchResultRuleDTO.setCheckItem(batchPlanTableLine.getCheckItem());
                    batchResultRuleRepository.insertDTOSelective(batchResultRuleDTO);

                }
            }

            for (BatchPlanField batchPlanField : fieldList) {
                List<BatchPlanFieldLine> lineList = batchPlanFieldLineRepository.select(BatchPlanFieldLine.FIELD_PLAN_FIELD_ID, batchPlanField.getPlanFieldId());
                for (BatchPlanFieldLine batchPlanFieldLine : lineList) {
                    List<PlanWarningLevel> warningLevelList = planWarningLevelRepository.select(
                            PlanWarningLevel.builder()
                                    .sourceId(batchPlanFieldLine.getPlanFieldLineId())
                                    .sourceType(PlanConstant.WARNING_LEVEL_FIELD)
                                    .build());

                    BatchPlanFieldLineDTO batchPlanFieldLineDTO = batchPlanFieldLineConverter.entityToDto(batchPlanFieldLine);
                    Measure measure = measureCollector.getMeasure(batchPlanFieldLine.getCheckItem().toUpperCase());

                    BatchResultRuleDTO batchResultRuleDTO = measure.check(MeasureParamDO.builder()
                            .tenantId(tenantId)
                            .datasourceDTO(datasourceDTO)
                            .batchPlanFieldLineDTO(batchPlanFieldLineDTO)
                            .warningLevelList(warningLevelList)
                            .batchResultBase(batchResultBase)
                            .build());
                    batchResultRuleDTO.setResultBaseId(batchResultBase.getResultBaseId());
                    batchResultRuleDTO.setRuleType("TABLE");
                    batchResultRuleDTO.setTableName(batchPlanBase.getTableName());
                    batchResultRuleDTO.setRuleId(batchPlanField.getPlanFieldId());
                    batchResultRuleDTO.setRuleCode(batchPlanField.getRuleCode());
                    batchResultRuleDTO.setRuleName(batchPlanField.getRuleName());
                    batchResultRuleDTO.setFieldName(batchPlanField.getFieldName());
                    batchResultRuleDTO.setCheckItem(batchPlanFieldLine.getCheckItem());
                    batchResultRuleRepository.insertDTOSelective(batchResultRuleDTO);

                }
            }

            //获取所有告警等级
            List<LovValueDTO> list = lovAdapter.queryLovValue("HDSP.XQUA.WARNING_LEVEL", tenantId);
            Map<String, BigDecimal> map = new HashMap<>();
            BigDecimal n = BigDecimal.valueOf(list.size());
            BigDecimal half = BigDecimal.valueOf(0.5);
            BigDecimal f = BigDecimal.ONE.divide(n, 2, RoundingMode.HALF_UP).multiply(half);
            for (LovValueDTO lovValueDTO : list) {
                map.put(lovValueDTO.getValue(), f);
                f = f.add(BigDecimal.ONE.divide(n, 2, RoundingMode.HALF_UP));
            }

            // 查询规则
            List<BatchResultRuleDTO> resultRuleDTOList = batchResultRuleRepository.selectByResultId(batchResult.getResultId());
            BigDecimal w = BigDecimal.valueOf(resultRuleDTOList.stream().mapToLong(BatchResultRuleDTO::getWeight).sum());
            BigDecimal sum = BigDecimal.ZERO;
            for (BatchResultRuleDTO batchResultRuleDTO : resultRuleDTOList) {
                BigDecimal multiply = BigDecimal.valueOf(batchResultRuleDTO.getWeight())
                        .divide(w, 2, RoundingMode.HALF_UP)
                        .multiply(map.get(batchResultRuleDTO.getWarningLevel()));
                sum = sum.add(multiply);
            }
            batchResult.setMark(sum.multiply(BigDecimal.valueOf(100)));
            batchResult.setPlanStatus(PlanConstant.PlanStatus.SUCCESS);
            batchResultRepository.updateByPrimaryKey(batchResult);
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
        ResponseUtils.getResponse(jobResult, JobDTO.class, (httpStatus, response) -> {
            throw new CommonException(response);
        }, exceptionResponse -> {
            throw new CommonException(exceptionResponse.getMessage());
        });
    }

    /**
     * 获取并设置表信息
     *
     * @param datasourceDTO
     * @param batchResultBase
     */
    private void setTableInfo(DatasourceDTO datasourceDTO, BatchResultBase batchResultBase) {
        datasourceDTO.setSql(String.format(TABLE_SQL, datasourceDTO.getSchema(), datasourceDTO.getTableName()));
        List<Map<String, Long>> result = ResponseUtils.getResponse(datasourceFeign.execSql(datasourceDTO.getTenantId(), datasourceDTO), new TypeReference<List<Map<String, Long>>>() {
        }, (httpStatus, response) -> {
            throw new CommonException(response);
        }, exceptionResponse -> {
            throw new CommonException(exceptionResponse.getMessage());
        });
        if (!result.isEmpty()) {
            batchResultBase.setDataCount(result.get(0).get("table_rows"));
            batchResultBase.setTableSize(result.get(0).get("data_length"));
        } else {
            throw new CommonException("查询表行数和大小失败，请联系系统管理员！");
        }
    }
}
