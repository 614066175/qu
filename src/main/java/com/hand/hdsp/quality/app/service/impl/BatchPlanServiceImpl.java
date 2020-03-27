package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.BatchPlanService;
import com.hand.hdsp.quality.domain.entity.*;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.converter.BatchPlanTableLineConverter;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.feign.DispatchJobFeign;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureCollector;
import io.choerodon.core.exception.CommonException;
import org.hzero.core.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>批数据评估方案表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Service
public class BatchPlanServiceImpl implements BatchPlanService {

    private static final String JOB_COMMAND = "curl -X GET --header 'Accept: */*' 'http://%s:%s/v2/%d/batch-plans/exec/%d'";

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
    private DispatchJobFeign dispatchJobFeign;
    @Autowired
    private InetUtils inetUtils;

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
        BatchPlanDTO batchPlanDTO = batchPlanRepository.selectDTOByPrimaryKey(planId);

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
                    .build();

            List<BatchPlanTable> tableList = batchPlanTableRepository.select(BatchPlanTable.FIELD_PLAN_BASE_ID, batchPlanBase.getPlanBaseId());

            //插入批数据方案结果表-表信息
            BatchResultBase batchResultBase = BatchResultBase.builder()
                    .resultId(batchResult.getResultId())
                    .tableName(batchPlanBase.getTableName())
                    .ruleCount((long) tableList.size())
                    .tenantId(tenantId)
                    .build();
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
                            .build());
                    batchResultRuleDTO.setResultBaseId(batchResultBase.getResultBaseId());
                    batchResultRuleDTO.setRuleType("TABLE");
                    batchResultRuleDTO.setTableName(batchPlanBase.getTableName());
                    batchResultRuleDTO.setRuleCode(batchPlanTable.getRuleCode());
                    batchResultRuleDTO.setRuleName(batchPlanTable.getRuleName());
                    batchResultRuleDTO.setCheckItem(batchPlanTableLine.getCheckItem());
                    batchResultRuleRepository.insertDTOSelective(batchResultRuleDTO);

                }
            }
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
}
