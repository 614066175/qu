package com.hand.hdsp.quality.app.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.driver.core.api.dto.PluginDatasourceDTO;
import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.BatchPlanService;
import com.hand.hdsp.quality.domain.entity.*;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.constant.WarningLevel;
import com.hand.hdsp.quality.infra.dataobject.BatchPlanFieldConDO;
import com.hand.hdsp.quality.infra.dataobject.BatchPlanTableConDO;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.dataobject.SpecifiedParamsResponseDO;
import com.hand.hdsp.quality.infra.feign.DispatchJobFeign;
import com.hand.hdsp.quality.infra.feign.DqRuleLineFeign;
import com.hand.hdsp.quality.infra.feign.RestJobFeign;
import com.hand.hdsp.quality.infra.feign.TimestampFeign;
import com.hand.hdsp.quality.infra.mapper.BatchResultItemMapper;
import com.hand.hdsp.quality.infra.mapper.BatchResultMapper;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureCollector;
import com.hand.hdsp.quality.infra.util.JsonUtils;
import com.hand.hdsp.quality.infra.util.ParamsUtil;
import com.hand.hdsp.quality.infra.vo.ResultWaringVO;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hzero.boot.alert.service.AlertMessageHandler;
import org.hzero.boot.alert.vo.InboundMessage;
import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.message.MessageAccessor;
import org.hzero.core.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>批数据评估方案表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Slf4j
@Service
public class BatchPlanServiceImpl implements BatchPlanService {

    private static final String JOB_URL = "/v2/%d/batch-plans/exec/%d";
    private static final String JOB_BODY = "{}";
    private static final String JOB_HEADER = "{\"content-type\": \"application/json\"}";
    private static final String JOB_SETTING_INFO = "{\"authSettingInfo\":{\"auth\":\"OAUTH2\",\"grantType\":\"PASSWORD\"},\"apiSettingInfo\":{\"retryEnabled\":false},\"callbackApiSettingInfo\":{\"enabled\":false}}";
    private static final String SQL_PACK = " (%s) sql_pack";
    @Value("${hdsp.route-data.service-short}")
    private String serviceShort;
    @Value("${hdsp.route-data.service-id}")
    private String serviceId;

    @Resource
    private BatchPlanBaseRepository batchPlanBaseRepository;
    @Resource
    private BatchPlanRepository batchPlanRepository;
    @Resource
    private BatchPlanTableRepository batchPlanTableRepository;
    @Resource
    private BatchPlanTableConRepository batchPlanTableConRepository;
    @Resource
    private BatchPlanFieldRepository batchPlanFieldRepository;
    @Resource
    private BatchPlanFieldConRepository batchPlanFieldConRepository;
    @Resource
    private BatchPlanRelTableRepository batchPlanRelTableRepository;
    @Resource
    private BatchResultRepository batchResultRepository;
    @Resource
    private BatchResultBaseRepository batchResultBaseRepository;
    @Resource
    private BatchResultRuleRepository batchResultRuleRepository;
    @Resource
    private BatchResultItemRepository batchResultItemRepository;
    @Resource
    private MeasureCollector measureCollector;
    @Resource
    private DispatchJobFeign dispatchJobFeign;
    @Resource
    private RestJobFeign restJobFeign;
    @Resource
    private LovAdapter lovAdapter;
    @Resource
    private DqRuleLineFeign dqRuleLineFeign;
    @Resource
    private MessageClient messageClient;
    @Resource
    private TimestampFeign timestampFeign;
    @Resource
    private BatchResultItemMapper batchResultItemMapper;
    @Resource
    private BatchResultMapper batchResultMapper;
    @Resource
    private AlertMessageHandler alertMessageHandler;

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
    @Transactional(rollbackFor = Exception.class)
    public void generate(Long tenantId, Long planId) {
        BatchPlanDTO batchPlanDTO = batchPlanRepository.selectDTOByPrimaryKey(planId);
        // 创建或更新job
        String jobName = String.format(PlanConstant.JOB_NAME, tenantId, batchPlanDTO.getPlanCode());
        ResponseEntity<String> jobResult = dispatchJobFeign.createOrUpdate(tenantId,
                JobDTO.builder()
                        .themeId(PlanConstant.DEFAULT_THEME_ID)
                        .layerId(PlanConstant.DEFAULT_LAYER_ID)
                        .jobName(jobName)
                        .jobDescription(batchPlanDTO.getPlanName())
                        .jobClass(PlanConstant.JOB_CLASS)
                        .jobType(PlanConstant.JOB_TYPE)
                        .jobLevel(PlanConstant.JOB_LEVEL)
                        .enabledFlag(1)
                        .tenantId(tenantId)
                        .build());
        ResponseUtils.getResponse(jobResult, JobDTO.class);

        //查询是否存在restJob
        ResponseEntity<String> findResult = restJobFeign.findName(tenantId, jobName);
        RestJobDTO restJobDTO = ResponseUtils.getResponse(findResult, RestJobDTO.class);

        restJobDTO.setExternal(0);
        restJobDTO.setMethod(PlanConstant.JOB_METHOD);
        restJobDTO.setServiceCode(serviceId);
        restJobDTO.setServiceName(serviceShort);
        restJobDTO.setUseGateway(1);
        restJobDTO.setUrl(String.format(JOB_URL, tenantId, planId));
        restJobDTO.setJobName(jobName);
        restJobDTO.setBody(JOB_BODY);
        restJobDTO.setHeader(JOB_HEADER);
        restJobDTO.setSettingInfo(JOB_SETTING_INFO);

        //插入或更新
        ResponseEntity<String> restJobResult = restJobFeign.create(tenantId, restJobDTO);
        ResponseUtils.getResponse(restJobResult, RestJobDTO.class);

        // 创建更新时间戳表
        List<BatchPlanBase> list = batchPlanBaseRepository.select(BatchPlanBase.builder().planId(planId).build());
        for (BatchPlanBase base : list) {
            if (!PlanConstant.IncrementStrategy.NONE.equals(base.getIncrementStrategy())) {

                ResponseEntity<String> timestampResult = timestampFeign.createOrUpdateTimestamp(tenantId,
                        TimestampControlDTO.builder()
                                .tenantId(tenantId)
                                .timestampType(String.format(PlanConstant.TIMESTAMP_TYPE, tenantId, batchPlanDTO.getPlanCode(), base.getPlanBaseId()))
                                .datasourceId(base.getDatasourceId())
                                .datasourceCode(base.getDatasourceCode())
                                .sourceTableName(base.getObjectName())
                                .sourceSchema(base.getDatasourceSchema())
                                .incrementStrategy(base.getIncrementStrategy())
                                .incrementColumn(base.getIncrementColumn())
                                .whereCondition(base.getWhereCondition())
                                .syncType(PlanConstant.JOB_CLASS)
                                .build());
                ResponseUtils.getResponse(timestampResult, TimestampControlDTO.class);
            }

        }

    }


    @Override
    public void sendMessage(Long planId) {
        HashMap<String, String> labels = new HashMap<>();
        List<ResultWaringVO> resultWaringVOS = batchResultItemMapper.selectWarningLevelByPlanId(planId);
        if(CollectionUtils.isEmpty(resultWaringVOS)){
            return;
        }
        List<String> warningLevels = resultWaringVOS.stream().map(ResultWaringVO::getWarningLevel).collect(Collectors.toList());
        BatchResultDTO batchResultDTO = batchResultMapper.selectByPlanId(planId);
        if (warningLevels.contains(WarningLevel.RED)) {
            BatchPlan batchPlan = batchPlanRepository.selectByPrimaryKey(planId);
            if ((batchPlan != null) && (batchPlan.getWarningCode() != null)) {
                List<String> exceptionInfos = resultWaringVOS.stream()
                        .filter(vo -> WarningLevel.RED.equals(vo.getWarningLevel()))
                        .map(ResultWaringVO::getExceptionInfo)
                        .collect(Collectors.toList());
                InboundMessage inboundMessage = new InboundMessage();
                inboundMessage.setAlertCode(String.format("%s-red", batchPlan.getWarningCode()));
                inboundMessage.setTenantId(batchResultDTO.getTenantId());
                labels.put("planName", batchResultDTO.getPlanName());
                labels.put("mark", batchResultDTO.getMark() != null ? batchResultDTO.getMark().toString() : "");
                labels.put("startDate", DateFormatUtils.format(batchResultDTO.getStartDate(), BaseConstants.Pattern.DATETIME));
                labels.put("status", batchResultDTO.getPlanStatus());
                labels.put("exceptionInfo", String.valueOf(exceptionInfos));
                labels.put("warningLevel", WarningLevel.RED);
                inboundMessage.setLabels(labels);
                alertMessageHandler.sendMessage(inboundMessage);
            }
        }
        if (warningLevels.contains(WarningLevel.YELLOW)) {
            BatchPlan batchPlan = batchPlanRepository.selectByPrimaryKey(planId);
            if ((batchPlan != null) && (batchPlan.getWarningCode() != null)) {
                List<String> exceptionInfos = resultWaringVOS.stream()
                        .filter(vo -> WarningLevel.YELLOW.equals(vo.getWarningLevel()))
                        .map(ResultWaringVO::getExceptionInfo)
                        .collect(Collectors.toList());
                InboundMessage inboundMessage = new InboundMessage();
                inboundMessage.setAlertCode(String.format("%s-yellow", batchPlan.getWarningCode()));
                inboundMessage.setTenantId(batchResultDTO.getTenantId());
                labels.put("planName", batchResultDTO.getPlanName());
                labels.put("mark", batchResultDTO.getMark() != null ? batchResultDTO.getMark().toString() : "");
                labels.put("startDate", DateFormatUtils.format(batchResultDTO.getStartDate(), BaseConstants.Pattern.DATETIME));
                labels.put("status", batchResultDTO.getPlanStatus());
                labels.put("exceptionInfo", String.valueOf(exceptionInfos));
                labels.put("warningLevel", WarningLevel.YELLOW);
                inboundMessage.setLabels(labels);
                alertMessageHandler.sendMessage(inboundMessage);
            }
        }
        if (warningLevels.contains(WarningLevel.ORANGE)) {
            BatchPlan batchPlan = batchPlanRepository.selectByPrimaryKey(planId);
            if ((batchPlan != null) && (batchPlan.getWarningCode() != null)) {
                List<String> exceptionInfos = resultWaringVOS.stream()
                        .filter(vo -> WarningLevel.ORANGE.equals(vo.getWarningLevel()))
                        .map(ResultWaringVO::getExceptionInfo)
                        .collect(Collectors.toList());
                InboundMessage inboundMessage = new InboundMessage();
                inboundMessage.setAlertCode(String.format("%s-orange", batchPlan.getWarningCode()));
                inboundMessage.setTenantId(batchResultDTO.getTenantId());
                labels.put("planName", batchResultDTO.getPlanName());
                labels.put("mark", batchResultDTO.getMark() != null ? batchResultDTO.getMark().toString() : "");
                labels.put("startDate", DateFormatUtils.format(batchResultDTO.getStartDate(), BaseConstants.Pattern.DATETIME));
                labels.put("status", batchResultDTO.getPlanStatus());
                labels.put("exceptionInfo", String.valueOf(exceptionInfos));
                labels.put("warningLevel", WarningLevel.ORANGE);
                inboundMessage.setLabels(labels);
                alertMessageHandler.sendMessage(inboundMessage);
            }
        }
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

        //时间戳对象list
        List<TimestampControlDTO> timestampList = new ArrayList<>();

        try {
            //规则计算
            ruleJudge(tenantId, planId, batchResult, timestampList);

            //分数计算
            countScore(tenantId, batchResult);

            //告警
            sendWarning(tenantId, batchResult.getResultId());

            //更新增量参数
            updateTimestamp(tenantId, timestampList, true);
        } catch (CommonException e) {
            //更新增量参数
            updateTimestamp(tenantId, timestampList, false);
            batchResult.setPlanStatus(PlanConstant.PlanStatus.FAILED);
            batchResult.setExceptionInfo(MessageAccessor.getMessage(e.getMessage(), e.getParameters()).getDesc());
            batchResultRepository.updateByPrimaryKeySelective(batchResult);
            log.error("exec plan error!", e);
            throw e;
        } catch (Exception e) {
            //更新增量参数
            updateTimestamp(tenantId, timestampList, false);
            batchResult.setPlanStatus(PlanConstant.PlanStatus.FAILED);
            batchResult.setExceptionInfo(ExceptionUtils.getMessage(e));
            batchResultRepository.updateByPrimaryKeySelective(batchResult);
            log.error("exec plan error!", e);
            throw e;
        }
    }

    /**
     * 更新增量参数
     *
     * @param tenantId
     * @param timestampList
     * @param success
     */
    private void updateTimestamp(Long tenantId, List<TimestampControlDTO> timestampList, boolean success) {
        if (success) {
            for (TimestampControlDTO timestampControlDTO : timestampList) {
                ResponseEntity<String> response = timestampFeign.updateIncrement(tenantId,
                        timestampControlDTO);
                ResponseUtils.getResponse(response, TimestampControlDTO.class);
            }
        } else {
            for (TimestampControlDTO timestampControlDTO : timestampList) {
                ResponseEntity<String> response = timestampFeign.updateIncrement(tenantId,
                        TimestampControlDTO.builder()
                                .timestampType(timestampControlDTO.getTimestampType())
                                .success(false)
                                .build());
                ResponseUtils.getResponse(response, TimestampControlDTO.class);
            }
        }

    }

    /**
     * 对每个规则进行计算
     *
     * @param tenantId    tenantId
     * @param planId      planId
     * @param batchResult batchResult
     */
    private void ruleJudge(Long tenantId, Long planId, BatchResult batchResult, List<TimestampControlDTO> timestampList) {

        List<BatchPlanBase> baseList = batchPlanBaseRepository.select(BatchPlanBase.FIELD_PLAN_ID, planId);
        for (BatchPlanBase batchPlanBase : baseList) {
            String objectName = batchPlanBase.getObjectName();


            PluginDatasourceDTO pluginDatasourceDTO=PluginDatasourceDTO.builder()
                    .datasourceId(batchPlanBase.getDatasourceId())
                    .tenantId(tenantId)
                    .datasourceCode(batchPlanBase.getDatasourceCode())
                    .build();



            String packageObjectName = objectName;
            if (PlanConstant.SqlType.SQL.equals(batchPlanBase.getSqlType())) {
                packageObjectName = String.format(SQL_PACK, objectName);
            }

            //插入批数据方案结果表-表信息
            BatchResultBase batchResultBase = BatchResultBase.builder()
                    .resultId(batchResult.getResultId())
                    .planBaseId(batchPlanBase.getPlanBaseId())
                    .objectName(objectName)
                    .packageObjectName(packageObjectName)
                    .datasourceType(batchPlanBase.getDatasourceType())
                    .ruleCount(0L)
                    .exceptionRuleCount(0L)
                    .checkItemCount(0L)
                    .exceptionCheckItemCount(0L)
                    .tenantId(tenantId)
                    .build();

            //获取增量参数，并更新 where 条件
            updateWhereCondition(tenantId, planId, batchPlanBase, batchResultBase, timestampList);

            batchResultBaseRepository.insertSelective(batchResultBase);

            handleTableRule(tenantId, batchResultBase, batchPlanBase.getDatasourceSchema(), pluginDatasourceDTO);
            handleFieldRule(tenantId, batchResultBase, batchPlanBase.getDatasourceSchema(), pluginDatasourceDTO);
            handleRelTableRule(tenantId, batchResultBase, batchPlanBase.getDatasourceSchema(), pluginDatasourceDTO);

            batchResultBaseRepository.updateByPrimaryKeySelective(batchResultBase);
        }
    }

    /**
     * 获取增量参数，并更新 where 条件
     *
     * @param tenantId
     * @param planId
     * @param batchPlanBase
     * @param batchResultBase
     * @param timestampList
     */
    private void updateWhereCondition(Long tenantId, Long planId, BatchPlanBase batchPlanBase, BatchResultBase batchResultBase, List<TimestampControlDTO> timestampList) {
        // 获取增量参数
        if (!PlanConstant.IncrementStrategy.NONE.equals(batchPlanBase.getIncrementStrategy())) {
            // 查询方案
            BatchPlan batchPlan = batchPlanRepository.selectByPrimaryKey(planId);
            String timestampType = String.format(PlanConstant.TIMESTAMP_TYPE, tenantId, batchPlan.getPlanCode(), batchPlanBase.getPlanBaseId());
            ResponseEntity<String> incrementParam = timestampFeign.getIncrementParam(tenantId, timestampType);
            SpecifiedParamsResponseDO specifiedParamsResponseDO = ResponseUtils.getResponse(incrementParam, SpecifiedParamsResponseDO.class);
            batchResultBase.setWhereCondition(ParamsUtil.handlePredefinedParams(batchPlanBase.getWhereCondition(), specifiedParamsResponseDO));

            //将增量参数添加到list中  全部处理完一起更新
            timestampList.add(TimestampControlDTO.builder()
                    .timestampType(timestampType)
                    .currentDateTime(specifiedParamsResponseDO.getCurrentDataTime())
                    .lastDateTime(specifiedParamsResponseDO.getLastDateTime())
                    .currentMaxId(specifiedParamsResponseDO.getCurrentMaxId())
                    .lastMaxId(specifiedParamsResponseDO.getLastMaxId())
                    .success(true)
                    .build());
        }
    }

    /**
     * 处理表级规则
     *
     * @param tenantId        tenantId
     * @param batchResultBase batchResultBase
     * @param schema   String
     */
    private void handleTableRule(Long tenantId, BatchResultBase batchResultBase, String schema, PluginDatasourceDTO pluginDatasourceDTO) {
        List<BatchPlanTable> tableList = batchPlanTableRepository.select(BatchPlanTable.FIELD_PLAN_BASE_ID, batchResultBase.getPlanBaseId());
        batchResultBase.setRuleCount(batchResultBase.getRuleCount() + tableList.size());

        for (BatchPlanTable batchPlanTable : tableList) {
            List<BatchPlanTableConDO> conList = batchPlanTableConRepository.selectJoinItem(BatchPlanTableConDO.builder().planRuleId(batchPlanTable.getPlanRuleId()).build());
            batchResultBase.setCheckItemCount(batchResultBase.getCheckItemCount() + conList.size());

            //异常标记
            boolean exceptionFlag = false;

            BatchResultRuleDTO batchResultRuleDTO = BatchResultRuleDTO.builder()
                    .resultBaseId(batchResultBase.getResultBaseId())
                    .ruleType(PlanConstant.ResultRuleType.TABLE)
                    .planRuleId(batchPlanTable.getPlanRuleId())
                    .ruleCode(batchPlanTable.getRuleCode())
                    .ruleName(batchPlanTable.getRuleName())
                    .ruleDesc(batchPlanTable.getRuleDesc())
                    .checkType(batchPlanTable.getCheckType())
                    .weight(batchPlanTable.getWeight())
                    .resultFlag(1)
                    .tenantId(tenantId)
                    .build();
            batchResultRuleRepository.insertDTOSelective(batchResultRuleDTO);

            for (BatchPlanTableConDO batchPlanTableConDO : conList) {
                //自定义SQL特殊转换
                if (PlanConstant.RuleType.SQL_CUSTOM.equals(batchPlanTable.getRuleType())) {
                    batchPlanTableConDO.setCheckItem(PlanConstant.RuleType.SQL_CUSTOM);
                }

                Measure measure = measureCollector.getMeasure(batchPlanTableConDO.getCheckItem().toUpperCase());

                MeasureParamDO param = MeasureParamDO.builder()
                        .conditionId(batchPlanTableConDO.getConditionId())
                        .tenantId(tenantId)
                        .checkItem(batchPlanTableConDO.getCheckItem())
                        .countType(batchPlanTableConDO.getCountType())
                        .compareWay(batchPlanTableConDO.getCompareWay())
                        .whereCondition(joinWhereCondition(batchPlanTableConDO.getWhereCondition(), batchResultBase.getWhereCondition()))
                        .warningLevelList(JsonUtils.json2WarningLevel(batchPlanTableConDO.getWarningLevel()))
                        .schema(schema)
                        .pluginDatasourceDTO(pluginDatasourceDTO)
                        .batchResultBase(batchResultBase)
                        .batchResultRuleDTO(batchResultRuleDTO)
                        .batchResultItem(BatchResultItem.builder().build())
                        .build();
                measure.check(param);

                BatchResultItem batchResultItem = param.getBatchResultItem();
                batchResultItem.setResultRuleId(batchResultRuleDTO.getResultRuleId());
                batchResultItem.setPlanLineId(batchPlanTableConDO.getPlanLineId());
                batchResultItem.setConditionId(batchPlanTableConDO.getConditionId());
                batchResultItem.setWhereCondition(batchPlanTableConDO.getWhereCondition());
                batchResultItem.setCompareWay(batchPlanTableConDO.getCompareWay());
                batchResultItem.setWarningLevelJson(batchPlanTableConDO.getWarningLevel());
                batchResultItem.setCheckItem(batchPlanTableConDO.getCheckItem());
                batchResultItem.setCountType(batchPlanTableConDO.getCountType());
                batchResultItem.setCustomSql(batchPlanTableConDO.getCustomSql());
                batchResultItem.setTenantId(tenantId);
                batchResultItemRepository.insertSelective(batchResultItem);

                if (StringUtils.isNotBlank(batchResultItem.getWarningLevel())) {
                    batchResultBase.setExceptionCheckItemCount(batchResultBase.getExceptionCheckItemCount() + 1);
                    exceptionFlag = true;

                    //异常阻断
                    if (batchPlanTable.getExceptionBlock() == 1) {
                        batchResultBase.setExceptionRuleCount(batchResultBase.getExceptionRuleCount() + 1);
                        batchResultBaseRepository.updateByPrimaryKeySelective(batchResultBase);
                        batchResultRuleDTO.setResultFlag(0);
                        batchResultRuleRepository.updateByDTOPrimaryKeySelective(batchResultRuleDTO);
                        throw new CommonException(ErrorCode.EXCEPTION_BLOCK);
                    }
                }
            }

            if (exceptionFlag) {
                batchResultBase.setExceptionRuleCount(batchResultBase.getExceptionRuleCount() + 1);
                batchResultRuleDTO.setResultFlag(0);
            }
            batchResultRuleRepository.updateByDTOPrimaryKeySelective(batchResultRuleDTO);
        }
    }

    /**
     * 处理字段规则
     *
     * @param tenantId        tenantId
     * @param batchResultBase batchResultBase
     * @param schema   String
     */
    private void handleFieldRule(Long tenantId, BatchResultBase batchResultBase, String schema, PluginDatasourceDTO pluginDatasourceDTO) {
        List<BatchPlanField> fieldList = batchPlanFieldRepository.select(BatchPlanTable.FIELD_PLAN_BASE_ID, batchResultBase.getPlanBaseId());
        batchResultBase.setRuleCount(batchResultBase.getRuleCount() + fieldList.size());

        for (BatchPlanField batchPlanField : fieldList) {
            List<BatchPlanFieldConDO> conList = batchPlanFieldConRepository.selectJoinItem(BatchPlanFieldConDO.builder().planRuleId(batchPlanField.getPlanRuleId()).build());
            batchResultBase.setCheckItemCount(batchResultBase.getCheckItemCount() + conList.size());

            //异常标记
            boolean exceptionFlag = false;
            BatchResultRuleDTO batchResultRuleDTO = BatchResultRuleDTO.builder()
                    .resultBaseId(batchResultBase.getResultBaseId())
                    .ruleType(PlanConstant.ResultRuleType.FIELD)
                    .planRuleId(batchPlanField.getPlanRuleId())
                    .ruleCode(batchPlanField.getRuleCode())
                    .ruleName(batchPlanField.getRuleName())
                    .ruleDesc(batchPlanField.getRuleDesc())
                    .checkType(batchPlanField.getCheckType())
                    .weight(batchPlanField.getWeight())
                    .resultFlag(1)
                    .tenantId(tenantId)
                    .build();
            batchResultRuleRepository.insertDTOSelective(batchResultRuleDTO);

            for (BatchPlanFieldConDO batchPlanFieldConDO : conList) {

                Measure measure;
                if (PlanConstant.CheckWay.COMMON.equals(batchPlanFieldConDO.getCheckWay())) {
                    measure = measureCollector.getMeasure(batchPlanFieldConDO.getCheckItem().toUpperCase());
                } else {
                    measure = measureCollector.getMeasure(PlanConstant.CheckWay.REGULAR);
                }

                MeasureParamDO param = MeasureParamDO.builder()
                        .tenantId(tenantId)
                        .conditionId(batchPlanFieldConDO.getConditionId())
                        .checkItem(batchPlanFieldConDO.getCheckItem())
                        .countType(batchPlanFieldConDO.getCountType())
                        .compareWay(batchPlanFieldConDO.getCompareWay())
                        .whereCondition(joinWhereCondition(batchPlanFieldConDO.getWhereCondition(), batchResultBase.getWhereCondition()))
                        .warningLevelList(JsonUtils.json2WarningLevel(batchPlanFieldConDO.getWarningLevel()))
                        .schema(schema)
                        .pluginDatasourceDTO(pluginDatasourceDTO)
                        .fieldName(batchPlanFieldConDO.getFieldName())
                        .checkFieldName(batchPlanFieldConDO.getCheckFieldName())
                        .regularExpression(batchPlanFieldConDO.getRegularExpression())
                        .batchResultBase(batchResultBase)
                        .batchResultRuleDTO(batchResultRuleDTO)
                        .batchResultItem(BatchResultItem.builder().build())
                        .build();
                measure.check(param);

                BatchResultItem batchResultItem = param.getBatchResultItem();
                batchResultItem.setResultRuleId(batchResultRuleDTO.getResultRuleId());
                batchResultItem.setPlanLineId(batchPlanFieldConDO.getPlanLineId());
                batchResultItem.setConditionId(batchPlanFieldConDO.getConditionId());
                batchResultItem.setWhereCondition(batchPlanFieldConDO.getWhereCondition());
                batchResultItem.setCompareWay(batchPlanFieldConDO.getCompareWay());
                batchResultItem.setCheckWay(batchPlanFieldConDO.getCheckWay());
                batchResultItem.setWarningLevelJson(batchPlanFieldConDO.getWarningLevel());
                batchResultItem.setCheckWay(batchPlanFieldConDO.getCheckWay());
                batchResultItem.setCheckItem(batchPlanFieldConDO.getCheckItem());
                batchResultItem.setCountType(batchPlanFieldConDO.getCountType());
                if (PlanConstant.CheckWay.REGULAR.equals(batchPlanFieldConDO.getCheckWay())) {
                    batchResultItem.setCheckItem(PlanConstant.CheckWay.REGULAR);
                    batchResultItem.setCountType(null);
                }
                batchResultItem.setFieldName(batchPlanFieldConDO.getFieldName());
                batchResultItem.setCheckFieldName(batchPlanFieldConDO.getCheckFieldName());
                batchResultItem.setCheckFieldName(batchPlanFieldConDO.getCheckFieldName());
                batchResultItem.setRegularExpression(batchPlanFieldConDO.getRegularExpression());

                batchResultItem.setTenantId(tenantId);
                batchResultItemRepository.insertSelective(batchResultItem);

                if (StringUtils.isNotBlank(batchResultItem.getWarningLevel())) {
                    batchResultBase.setExceptionCheckItemCount(batchResultBase.getExceptionCheckItemCount() + 1);
                    exceptionFlag = true;

                    //异常阻断
                    if (batchPlanField.getExceptionBlock() == 1) {
                        batchResultBase.setExceptionRuleCount(batchResultBase.getExceptionRuleCount() + 1);
                        batchResultBaseRepository.updateByPrimaryKeySelective(batchResultBase);
                        batchResultRuleDTO.setResultFlag(0);
                        batchResultRuleRepository.updateByDTOPrimaryKeySelective(batchResultRuleDTO);
                        throw new CommonException(ErrorCode.EXCEPTION_BLOCK);
                    }
                }
            }

            if (exceptionFlag) {
                batchResultBase.setExceptionRuleCount(batchResultBase.getExceptionRuleCount() + 1);
                batchResultRuleDTO.setResultFlag(0);
            }
            batchResultRuleRepository.updateByDTOPrimaryKeySelective(batchResultRuleDTO);

        }
    }

    /**
     * 处理表间规则
     *
     * @param tenantId        tenantId
     * @param batchResultBase batchResultBase
     * @param schema   String
     */
    private void handleRelTableRule(Long tenantId, BatchResultBase batchResultBase, String schema, PluginDatasourceDTO pluginDatasourceDTO) {

        List<BatchPlanRelTable> relTableList = batchPlanRelTableRepository.select(BatchPlanTable.FIELD_PLAN_BASE_ID, batchResultBase.getPlanBaseId());
        batchResultBase.setRuleCount(batchResultBase.getRuleCount() + relTableList.size());
        batchResultBase.setCheckItemCount(batchResultBase.getCheckItemCount() + relTableList.size());

        for (BatchPlanRelTable batchPlanRelTable : relTableList) {
            Measure measure = measureCollector.getMeasure(PlanConstant.RuleType.TABLE_RELATION);

            MeasureParamDO param = MeasureParamDO.builder()
                    .tenantId(tenantId)
                    .schema(schema)
                    .pluginDatasourceDTO(pluginDatasourceDTO)
                    .batchPlanRelTable(batchPlanRelTable)
                    .batchResultBase(batchResultBase)
                    .batchResultItem(BatchResultItem.builder().build())
                    .build();
            measure.check(param);

            BatchResultRuleDTO batchResultRuleDTO = BatchResultRuleDTO.builder()
                    .resultBaseId(batchResultBase.getResultBaseId())
                    .ruleType(PlanConstant.ResultRuleType.REL_TABLE)
                    .planRuleId(batchPlanRelTable.getPlanRuleId())
                    .ruleCode(batchPlanRelTable.getRuleCode())
                    .ruleName(batchPlanRelTable.getRuleName())
                    .ruleDesc(batchPlanRelTable.getRuleDesc())
                    .checkType(batchPlanRelTable.getCheckType())
                    .weight(batchPlanRelTable.getWeight())
                    .resultFlag(1)
                    .tenantId(tenantId)
                    .build();
            batchResultRuleRepository.insertDTOSelective(batchResultRuleDTO);

            BatchResultItem batchResultItem = param.getBatchResultItem();
            batchResultItem.setResultRuleId(batchResultRuleDTO.getResultRuleId());
            batchResultItem.setRelDatasourceType(batchPlanRelTable.getRelDatasourceType());
            batchResultItem.setRelDatasourceId(batchPlanRelTable.getRelDatasourceId());
            batchResultItem.setRelSchema(batchPlanRelTable.getRelSchema());
            batchResultItem.setRelTableName(batchPlanRelTable.getRelTableName());
            batchResultItem.setWhereCondition(batchPlanRelTable.getWhereCondition());
            batchResultItem.setRelationship(batchPlanRelTable.getRelationship());
            batchResultItem.setWarningLevelJson(batchPlanRelTable.getWarningLevel());
            batchResultItem.setTenantId(tenantId);
            batchResultItemRepository.insertSelective(batchResultItem);

            if (StringUtils.isNotBlank(batchResultItem.getWarningLevel())) {
                batchResultRuleDTO.setResultFlag(0);
                batchResultRuleRepository.updateByDTOPrimaryKeySelective(batchResultRuleDTO);

                batchResultBase.setExceptionRuleCount(batchResultBase.getExceptionRuleCount() + 1);
                batchResultBase.setExceptionCheckItemCount(batchResultBase.getExceptionCheckItemCount() + 1);

                //异常阻断
                if (batchPlanRelTable.getExceptionBlock() == 1) {
                    batchResultBaseRepository.updateByPrimaryKeySelective(batchResultBase);
                    throw new CommonException(ErrorCode.EXCEPTION_BLOCK);
                }
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
        Map<String, BigDecimal> map = new HashMap<>(8);
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
        List<BatchResultItemDTO> itemDTOList = batchResultItemRepository.selectByResultId(batchResult.getResultId());
        BigDecimal w = BigDecimal.valueOf(itemDTOList.stream().mapToLong(BatchResultItemDTO::getWeight).sum());
        BigDecimal sum = BigDecimal.ZERO;
        for (BatchResultItemDTO batchResultItemDTO : itemDTOList) {
            BigDecimal multiply = BigDecimal.valueOf(batchResultItemDTO.getWeight())
                    .divide(w, 10, RoundingMode.HALF_UP)
                    .multiply(map.get(batchResultItemDTO.getWarningLevel()));
            sum = sum.add(multiply);
        }
        batchResult.setMark(sum.multiply(BigDecimal.valueOf(100)));
        batchResult.setPlanStatus(PlanConstant.PlanStatus.SUCCESS);
        batchResult.setEndDate(new Date());
        batchResultRepository.updateByPrimaryKeySelective(batchResult);
    }


    /**
     * 告警
     *
     * @param tenantId
     * @param resultId
     */
    private void sendWarning(Long tenantId, Long resultId) {
        BatchResultDTO batchResultDTO = batchResultRepository.showResultHead(BatchResultDTO.builder().resultId(resultId).tenantId(tenantId).build());
        //当没有告警发送代码时直接返回
        if (StringUtils.isBlank(batchResultDTO.getWarningCode())) {
            return;
        }
        ResponseEntity<String> result = dqRuleLineFeign.listAll(tenantId, DqRuleLineDTO.builder().configCode(batchResultDTO.getWarningCode()).build());
        List<DqRuleLineDTO> dqRuleLineDTOList = ResponseUtils.getResponse(result, new TypeReference<List<DqRuleLineDTO>>() {
        });

        //当未配置告警规则时直接返回
        if (CollectionUtils.isEmpty(dqRuleLineDTOList)) {
            return;
        }

        List<String> waringLevelStrings = batchResultItemRepository.selectWaringLevelByResultId(resultId);

        //当告警等级数组为空时直接返回（正常记录）
        if (CollectionUtils.isEmpty(waringLevelStrings)) {
            return;
        }

        Map<String, List<DqRuleLineDTO>> alertLevelMap = dqRuleLineDTOList.stream()
                .filter(dto -> dto.getAlertLevel() != null)
                .collect(Collectors.groupingBy(DqRuleLineDTO::getAlertLevel));

        if (MapUtils.isEmpty(alertLevelMap)) {
            return;
        }

        // TODO 目前只支持string类型参数，后续会增加object参数
        //查询表结果
//      List<BatchResultBaseDTO> resultBaseList = batchResultBaseRepository.listResultBase(BatchResultBaseDTO.builder().resultId(resultId).tenantId(tenantId).build());

        //查询规则结果

        //参数
        Map<String, String> args = new HashMap<>(5);
        args.put("planName", batchResultDTO.getPlanName());
        args.put("mark", batchResultDTO.getMark() != null ? batchResultDTO.getMark().toString() : "");
        args.put("startDate", DateFormatUtils.format(batchResultDTO.getStartDate(), BaseConstants.Pattern.DATETIME));
        args.put("status", lovAdapter.queryLovMeaning(PlanConstant.LOV_PLAN_STATUS, tenantId, batchResultDTO.getPlanStatus()));
        args.put("exceptionInfo", batchResultDTO.getExceptionInfo() != null ? batchResultDTO.getExceptionInfo() : "");

        for (String waringLevel : waringLevelStrings) {
            List<DqRuleLineDTO> list = alertLevelMap.get(waringLevel);
            for (DqRuleLineDTO dqRuleLineDTO : list) {
                //发送消息
                MessageSender messageSender = MessageSender.builder()
                        .messageCode(dqRuleLineDTO.getMessageTemplateCode())
                        .receiverTypeCode(dqRuleLineDTO.getReceiveGroupCode())
                        .receiverAddressList(messageClient.receiver(dqRuleLineDTO.getReceiveGroupCode(), null))
                        .typeCodeList(Collections.singletonList(dqRuleLineDTO.getAlarmWay()))
                        .args(args)
                        .tenantId(tenantId)
                        .build();
                messageClient.async().sendMessage(messageSender);
            }
        }
    }

    /**
     * 拼接where条件
     *
     * @param where1
     * @param where2
     * @return
     */
    private String joinWhereCondition(String where1, String where2) {
        if (StringUtils.isBlank(where1) && StringUtils.isBlank(where2)) {
            return null;
        }
        if (StringUtils.isNotBlank(where1) && StringUtils.isBlank(where2)) {
            return where1;
        }
        if (StringUtils.isBlank(where1) && StringUtils.isNotBlank(where2)) {
            return where2;
        }
        return where1 + " and " + where2;
    }

    public void setAlertMessageHandler(AlertMessageHandler alertMessageHandler) {
        this.alertMessageHandler = alertMessageHandler;
    }
}
