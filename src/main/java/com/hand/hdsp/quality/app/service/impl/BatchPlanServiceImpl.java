package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.BatchPlanService;
import com.hand.hdsp.quality.domain.entity.*;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.AlertTemplate;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.constant.PlanConstant.CompareSymbol;
import com.hand.hdsp.quality.infra.constant.PlanConstant.ExceptionParam;
import com.hand.hdsp.quality.infra.dataobject.BatchPlanFieldConDO;
import com.hand.hdsp.quality.infra.dataobject.BatchPlanTableConDO;
import com.hand.hdsp.quality.infra.dataobject.MeasureParamDO;
import com.hand.hdsp.quality.infra.dataobject.SpecifiedParamsResponseDO;
import com.hand.hdsp.quality.infra.feign.DispatchJobFeign;
import com.hand.hdsp.quality.infra.feign.LineageFeign;
import com.hand.hdsp.quality.infra.feign.RestJobFeign;
import com.hand.hdsp.quality.infra.feign.TimestampFeign;
import com.hand.hdsp.quality.infra.mapper.BatchResultItemMapper;
import com.hand.hdsp.quality.infra.mapper.BatchResultMapper;
import com.hand.hdsp.quality.infra.measure.Measure;
import com.hand.hdsp.quality.infra.measure.MeasureCollector;
import com.hand.hdsp.quality.infra.util.CustomThreadPool;
import com.hand.hdsp.quality.infra.util.JsonUtils;
import com.hand.hdsp.quality.infra.util.ParamsUtil;
import com.hand.hdsp.quality.infra.vo.ResultWaringVO;
import com.hand.hdsp.quality.infra.vo.WarningLevelVO;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.alert.service.AlertMessageHandler;
import org.hzero.boot.alert.vo.InboundMessage;
import org.hzero.boot.driver.api.dto.PluginDatasourceDTO;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.message.MessageAccessor;
import org.hzero.core.util.ResponseUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.driver.core.session.DriverSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.hand.hdsp.quality.infra.constant.PlanConstant.SqlType.TABLE;

/**
 * <p>
 * 批数据评估方案表应用服务默认实现
 * </p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Slf4j
@Service
public class BatchPlanServiceImpl implements BatchPlanService {

    private static final String JOB_URL = "/v2/%d/batch-plans/exec/%d";
    private static final String JOB_BODY = "{}";
    private static final String JOB_HEADER = "{\"content-type\": \"application/json\"}";
    private static final String JOB_SETTING_INFO =
            "{\"authSettingInfo\":{\"auth\":\"OAUTH2\",\"grantType\":\"PASSWORD\"},\"apiSettingInfo\":{\"retryEnabled\":false},\"callbackApiSettingInfo\":{\"enabled\":false}}";
    private static final String SQL_PACK = " (%s) sql_pack";
    private static final String ALERT_CODE = "%s-%s";
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
    private BatchPlanTableLineRepository batchPlanTableLineRepository;
    @Resource
    private MeasureCollector measureCollector;
    @Resource
    private DispatchJobFeign dispatchJobFeign;
    @Resource
    private RestJobFeign restJobFeign;
    @Resource
    private LovAdapter lovAdapter;
    @Resource
    private TimestampFeign timestampFeign;
    @Resource
    private BatchResultItemMapper batchResultItemMapper;
    @Resource
    private BatchResultMapper batchResultMapper;
    @Resource
    private AlertMessageHandler alertMessageHandler;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Resource
    private DriverSessionService driverSessionService;

    @Resource
    private LineageFeign lineageFeign;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(BatchPlanDTO batchPlanDTO) {
        List<BatchPlanBaseDTO> batchPlanBaseDTOList =
                batchPlanBaseRepository.selectDTO(BatchPlanBase.FIELD_PLAN_ID, batchPlanDTO.getPlanId());
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

        //生成数据质量command
        String jobCommand = generateCommand(batchPlanDTO);

        ResponseEntity<String> jobResult = dispatchJobFeign.createOrUpdate(tenantId,
                JobDTO.builder().themeId(PlanConstant.DEFAULT_THEME_ID).layerId(PlanConstant.DEFAULT_LAYER_ID)
                        .jobName(jobName).jobCommand(jobCommand).jobDescription(batchPlanDTO.getPlanName())
                        .jobClass(PlanConstant.JOB_CLASS).jobType(PlanConstant.JOB_TYPE)
                        .jobLevel(PlanConstant.JOB_LEVEL).enabledFlag(1).tenantId(tenantId).build());
        ResponseUtils.getResponse(jobResult, JobDTO.class);

        // 查询是否存在restJob
//        ResponseEntity<String> findResult = restJobFeign.findName(tenantId, jobName);
//        RestJobDTO restJobDTO = ResponseUtils.getResponse(findResult, RestJobDTO.class);
//
//        restJobDTO.setExternal(0);
//        restJobDTO.setMethod(PlanConstant.JOB_METHOD);
//        restJobDTO.setServiceCode(serviceId);
//        restJobDTO.setServiceName(serviceShort);
//        restJobDTO.setUseGateway(1);
//        restJobDTO.setUrl(String.format(JOB_URL, tenantId, planId));
//        restJobDTO.setJobName(jobName);
//        restJobDTO.setBody(JOB_BODY);
//        restJobDTO.setHeader(JOB_HEADER);
//        restJobDTO.setSettingInfo(JOB_SETTING_INFO);
//
//        // 插入或更新
//        ResponseEntity<String> restJobResult = restJobFeign.create(tenantId, restJobDTO);
//        ResponseUtils.getResponse(restJobResult, RestJobDTO.class);

        // 保存jobName到BatchPlan中
        batchPlanDTO.setPlanJobName(jobName);
        batchPlanRepository.updateByDTOPrimaryKeySelective(batchPlanDTO);

        // 创建更新时间戳表
        List<BatchPlanBase> list = batchPlanBaseRepository.select(BatchPlanBase.builder().planId(planId).build());
        for (BatchPlanBase base : list) {
            if (!PlanConstant.IncrementStrategy.NONE.equals(base.getIncrementStrategy())) {

                ResponseEntity<String> timestampResult = timestampFeign.createOrUpdateTimestamp(tenantId,
                        TimestampControlDTO.builder().tenantId(tenantId)
                                .timestampType(String.format(PlanConstant.TIMESTAMP_TYPE, tenantId,
                                        batchPlanDTO.getPlanCode(), base.getPlanBaseId()))
                                .datasourceId(base.getDatasourceId())
                                .datasourceCode(base.getDatasourceCode())
                                .sourceTableName(base.getObjectName())
                                .sourceSchema(base.getDatasourceSchema())
                                .incrementStrategy(base.getIncrementStrategy())
                                .incrementColumn(base.getIncrementColumn())
                                .whereCondition(base.getWhereCondition())
                                .syncType(PlanConstant.JOB_CLASS).build());
                ResponseUtils.getResponse(timestampResult, TimestampControlDTO.class);
            }

        }

    }

    /**
     * 生成job命令
     *
     * @param batchPlanDTO BatchPlanDTO
     * @return command
     */
    private String generateCommand(BatchPlanDTO batchPlanDTO) {
        StringBuilder builder = new StringBuilder();
        builder.append("type=rest\n")
                .append("rest.grantType=").append("IMPLICIT").append("\n")
                .append("rest.responseType=").append("IMPLICIT").append("\n")
                .append("rest.clientId=").append("client").append("\n")
                .append("rest.tokenUri=").append("/xoau/public/custom-token/oauth").append("\n")
                .append("rest.username=").append("admin").append("\n")
                .append("rest.external=false\n")
                .append("rest.app=" + serviceId + "\n")
                .append("rest.useGateway=true\n")
                .append("rest.uri=/" + serviceShort + "/v1/")
                .append(batchPlanDTO.getTenantId())
                .append("/batch-plans/exec/")
                .append(batchPlanDTO.getPlanId() + "\n")
                .append("rest.method=GET\n")
                .append("rest.contentType=application/json\n")
                .append("rest.body={}\n")
                .append("rest.auth=OAUTH2\n")
                .append("rest.retry.enabled=false\n")
                .append("rest.callback.enabled=false\n");
        return builder.toString();
    }


    @Override
    public void sendMessage(Long planId, Long resultId) {
        List<ResultWaringVO> resultWaringVOS = batchResultItemMapper.selectWarningLevelByResultId(resultId);
        if (CollectionUtils.isEmpty(resultWaringVOS)) {
            return;
        }
        List<String> warningLevels = new ArrayList<>();
        for (ResultWaringVO resultWaringVO : resultWaringVOS) {
            List<WarningLevelVO> warningLevelVOS = JsonUtils.json2WarningLevelVO(resultWaringVO.getWarningLevel());
            resultWaringVO.setWarningLevelVOList(warningLevelVOS);
            warningLevels.addAll(warningLevelVOS.stream().map(WarningLevelVO::getWarningLevel).distinct()
                    .collect(Collectors.toList()));
        }
        warningLevels = warningLevels.stream().distinct().collect(Collectors.toList());
        BatchResultDTO batchResultDTO = batchResultMapper.selectByPlanId(planId);
        warningLevels.forEach(warningLevel -> doSendMessage(planId, resultWaringVOS, batchResultDTO, warningLevel));
    }

    private void doSendMessage(Long planId, List<ResultWaringVO> resultWaringVOS, BatchResultDTO batchResultDTO,
                               String warningLevel) {
        HashMap<String, String> labels = new HashMap<>();
        BatchPlan batchPlan = batchPlanRepository.selectByPrimaryKey(planId);
        if (Objects.nonNull(batchPlan) && Strings.isNotEmpty(batchPlan.getWarningCode())) {
            List<String> exceptionInfos =
                    resultWaringVOS.stream().filter(vo -> warningLevel.equals(vo.getWarningLevel()))
                            .map(ResultWaringVO::getExceptionInfo).collect(Collectors.toList());
            InboundMessage inboundMessage = new InboundMessage();
            inboundMessage.setAlertCode(
                    String.format(ALERT_CODE, batchPlan.getWarningCode(), warningLevel.toLowerCase()));
            inboundMessage.setTenantId(batchResultDTO.getTenantId());
            labels.put(AlertTemplate.PLAN_NAME, batchResultDTO.getPlanName());
            labels.put(AlertTemplate.MARK, batchResultDTO.getMark() != null ? batchResultDTO.getMark().toString() : "");
            labels.put(AlertTemplate.START_DATE,
                    DateFormatUtils.format(batchResultDTO.getStartDate(), BaseConstants.Pattern.DATETIME));
            labels.put(AlertTemplate.STATUS, batchResultDTO.getPlanStatus());
            labels.put(AlertTemplate.EXCEPTION_INFO, String.valueOf(exceptionInfos));
            labels.put(AlertTemplate.WARNING_LEVEL, warningLevel);

            //查询每个resultBase的详细情况
            List<Map<String, Object>> dataSet = new ArrayList<>();
            List<BatchResultBaseDTO> batchResultBaseDTOList = batchResultBaseRepository.selectDTOByCondition(Condition
                    .builder(BatchResultBase.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(BatchResultBase.FIELD_RESULT_ID, batchResultDTO.getResultId())
                            .andEqualTo(BatchResultBase.FIELD_TENANT_ID, batchResultDTO.getTenantId()))
                    .build());
            // 根据每个base去查询具体的校验情况
            if (CollectionUtils.isNotEmpty(batchResultBaseDTOList)) {
                batchResultBaseDTOList.forEach(batchResultBaseDTO -> {
                    //获取base下所有校验项的告警等级Json
                    getResultWarningVOList(batchResultBaseDTO);
                    //获取base的规则
                    getRule(batchResultBaseDTO);
                });
                batchResultDTO.setBatchResultBaseDTOList(batchResultBaseDTOList);
                Map<String, Object> map = new HashMap<>();
                map.put("batchResult", batchResultDTO);
                dataSet.add(map);
            }
            inboundMessage.setLabels(labels);
            inboundMessage.setDataSet(dataSet);
            //同步告警走的是alert服务的handler，异步告警走的是event服务,生成事件消息处理
//            inboundMessage.setAsyncFlag(1);
            alertMessageHandler.sendMessage(inboundMessage);
        }
    }

    private void getRule(BatchResultBaseDTO batchResultBaseDTO) {
        List<BatchResultRuleDTO> batchResultRuleDTOList = batchResultRuleRepository.selectDTOByCondition(Condition.builder(BatchResultRule.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(BatchResultRule.FIELD_RESULT_BASE_ID, batchResultBaseDTO.getResultBaseId())
                        .andEqualTo(BatchResultRule.FIELD_TENANT_ID, batchResultBaseDTO.getTenantId()))
                .build());
        //获取表级规则
        List<BatchResultRuleDTO> tableList = batchResultRuleDTOList.stream()
                .filter(batchResultRuleDTO -> PlanConstant.ResultRuleType.TABLE.equals(batchResultRuleDTO.getRuleType()))
                .collect(Collectors.toList());
        List<BatchResultItemDTO> tableItemList = getItemList(tableList);
        //内置表级规则表格头
        List<BatchResultItemDTO> tableResult = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tableItemList)) {
            tableResult.add(0, BatchResultItemDTO.builder()
                    .ruleName("规则名称")
                    .warningLevel("告警结果等级")
                    .checkItem("检验项")
                    .countType("")
                    .whereCondition("启用条件")
                    .compareWay("比较方式")
                    .warningLevelJson("告警规则")
                    .actualValue("实际值")
                    .exceptionInfo("错误信息")
                    .build());
            CollectionUtils.addAll(tableResult, tableItemList);
        }
        batchResultBaseDTO.setTableResult(tableResult);
        //获取字段规则
        List<BatchResultRuleDTO> fieldList = batchResultRuleDTOList.stream()
                .filter(batchResultRuleDTO -> PlanConstant.ResultRuleType.FIELD.equals(batchResultRuleDTO.getRuleType()))
                .collect(Collectors.toList());
        List<BatchResultItemDTO> fieldItemList = getItemList(fieldList);
        List<BatchResultItemDTO> fieldResult = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(fieldItemList)) {
            fieldResult.add(0, BatchResultItemDTO.builder()
                    .ruleName("规则名称")
                    .warningLevel("告警结果等级")
                    .checkItem("检验项")
                    .countType("")
                    .fieldName("列名")
                    .whereCondition("启用条件")
                    .compareWay("比较方式")
                    .warningLevelJson("告警规则")
                    .actualValue("实际值")
                    .exceptionInfo("错误信息")
                    .build());
            CollectionUtils.addAll(fieldResult, fieldItemList);
        }
        batchResultBaseDTO.setFieldResult(fieldResult);
        //获取表间
        List<BatchResultRuleDTO> tableRelList = batchResultRuleDTOList.stream()
                .filter(batchResultRuleDTO -> PlanConstant.ResultRuleType.REL_TABLE.equals(batchResultRuleDTO.getRuleType()))
                .collect(Collectors.toList());
        List<BatchResultItemDTO> tableRelItemList = getItemList(tableRelList);
        tableRelItemList.forEach(batchResultItemDTO -> batchResultItemDTO.setObjectName(batchResultBaseDTO.getObjectName()));
        List<BatchResultItemDTO> tableRelResult = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tableRelItemList)) {
            tableRelResult.add(0, BatchResultItemDTO.builder()
                    .ruleName("规则名称")
                    .warningLevel("告警结果等级")
                    .warningLevelJson("告警规则")
                    .whereCondition("启用条件")
                    .relTableName("目标表")
                    .objectName("源表")
                    .actualValue("实际值")
                    .exceptionInfo("错误信息")
                    .build());
            CollectionUtils.addAll(tableRelResult, tableRelItemList);
        }
        batchResultBaseDTO.setTableRelResult(tableRelResult);
    }

    private List<BatchResultItemDTO> getItemList(List<BatchResultRuleDTO> list) {
        List<LovValueDTO> warningLevels = lovAdapter.queryLovValue(PlanConstant.LOV_WARNING_LEVEL, BaseConstants.DEFAULT_TENANT_ID);
        List<LovValueDTO> checkItems = lovAdapter.queryLovValue(PlanConstant.LOV_CHECK_ITEM, BaseConstants.DEFAULT_TENANT_ID);
        List<LovValueDTO> countTypes = lovAdapter.queryLovValue(PlanConstant.LOV_COUNT_TYPE, BaseConstants.DEFAULT_TENANT_ID);
        List<LovValueDTO> compareWays = lovAdapter.queryLovValue(PlanConstant.LOV_COMPARE_WAY, BaseConstants.DEFAULT_TENANT_ID);
        List<LovValueDTO> compareSymbols = lovAdapter.queryLovValue(PlanConstant.LOV_COMPARE_SYMBOL, BaseConstants.DEFAULT_TENANT_ID);
        List<BatchResultItemDTO> itemDTOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (BatchResultRuleDTO dto : list) {
                List<BatchResultItemDTO> batchResultItemDTOList = batchResultItemRepository.selectDTOByCondition(Condition.builder(BatchResultItem.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(BatchResultItem.FIELD_RESULT_RULE_ID, dto.getResultRuleId())
                                .andEqualTo(BatchResultItem.FIELD_TENANT_ID, dto.getTenantId()))
                        .build());
                batchResultItemDTOList.forEach(batchResultItemDTO -> {
                    batchResultItemDTO.setCountType(Optional.ofNullable(batchResultItemDTO.getCountType()).orElse(""));
                    batchResultItemDTO.setWhereCondition(Optional.ofNullable(batchResultItemDTO.getWhereCondition()).orElse(""));
                    batchResultItemDTO.setActualValue(Optional.ofNullable(batchResultItemDTO.getActualValue()).orElse(""));
                    batchResultItemDTO.setRuleName(dto.getRuleName());
                    //将告警等级装转换成中文
                    StringBuilder warningLevel = new StringBuilder();
                    List<WarningLevelVO> warningLevelVOList = JsonUtils.json2WarningLevelVO(batchResultItemDTO.getWarningLevel());
                    for (int i = 0; i < warningLevelVOList.size(); i++) {
                        for (LovValueDTO lovValueDTO : warningLevels) {
                            if (lovValueDTO.getValue().equals(warningLevelVOList.get(i).getWarningLevel())) {
                                warningLevel.append(String.format("告警配置项-%d:%s:%d个 ", i + 1, lovValueDTO.getMeaning(), warningLevelVOList.get(i).getLevelCount()))
                                        .append("<br>");
                            }
                        }
                    }
                    batchResultItemDTO.setWarningLevel(warningLevel.toString());
                    //将校验项转中文
                    checkItems.forEach(lovValueDTO -> {
                        if (lovValueDTO.getValue().equals(batchResultItemDTO.getCheckItem())) {
                            batchResultItemDTO.setCheckItem(lovValueDTO.getMeaning());
                        }
                    });
                    countTypes.forEach(lovValueDTO -> {
                        if (lovValueDTO.getValue().equals(batchResultItemDTO.getCountType())) {
                            batchResultItemDTO.setCountType(lovValueDTO.getMeaning());
                        }
                    });
                    compareWays.forEach(lovValueDTO -> {
                        if (lovValueDTO.getValue().equals(batchResultItemDTO.getCompareWay())) {
                            batchResultItemDTO.setCompareWay(lovValueDTO.getMeaning());
                        }
                    });
                    //将告警规则配置转为中文
                    StringBuilder warningLevelJson = new StringBuilder();
                    List<WarningLevelDTO> warningLevelDTOList = JsonUtils.json2WarningLevel(batchResultItemDTO.getWarningLevelJson());
                    for (int i = 0; i < warningLevelDTOList.size(); i++) {
                        WarningLevelDTO warningLevelDTO = warningLevelDTOList.get(i);
                        for (LovValueDTO lovValueDTO : warningLevels) {
                            if (lovValueDTO.getValue().equals(warningLevelDTO.getWarningLevel())) {
                                warningLevelJson.append(String.format("告警配置项-%d: %s: ", i + 1, lovValueDTO.getMeaning()));
                            }
                        }
                        //比较符转中文
                        for (LovValueDTO lovValueDTO : compareSymbols) {
                            if (lovValueDTO.getValue().equals(warningLevelDTO.getCompareSymbol())) {
                                warningLevelJson.append(lovValueDTO.getMeaning());
                            }
                        }
                        if (CompareSymbol.EQUAL.equals(warningLevelDTO.getCompareSymbol())
                                || CompareSymbol.NOT_EQUAL.equals(warningLevelDTO.getCompareSymbol())) {
                            warningLevelJson.append("( ");
                            if (StringUtils.isNotEmpty(warningLevelDTO.getExpectedValue())) {
                                warningLevelJson.append(warningLevelDTO.getExpectedValue());
                            } else {
                                warningLevelJson.append(Optional.ofNullable(warningLevelDTO.getStartValue()).orElse(""))
                                        .append("-")
                                        .append(Optional.ofNullable(warningLevelDTO.getEndValue()).orElse(""));
                            }
                            warningLevelJson.append(" )").append("<br>");
                        }
                        batchResultItemDTO.setWarningLevelJson(warningLevelJson.toString());
                    }
                });
                CollectionUtils.addAll(itemDTOList, batchResultItemDTOList);
            }
        }
        return itemDTOList;
    }


    private void getResultWarningVOList(BatchResultBaseDTO batchResultBaseDTO) {
        List<LovValueDTO> warningLevels = lovAdapter.queryLovValue(PlanConstant.LOV_WARNING_LEVEL, BaseConstants.DEFAULT_TENANT_ID);
        List<String> warningLevelJsonList = batchResultItemMapper.selectWaringLevelJson(batchResultBaseDTO);
        //将所有告警等级Json转换合并成集合
        List<WarningLevelVO> warningLevelVOList = new ArrayList<>();
        warningLevelJsonList.forEach(warningLevelJson -> warningLevelVOList.addAll(JsonUtils.json2WarningLevelVO(warningLevelJson)));
        //合并处理每个检验项的告警等级
        Map<String, Long> collect = warningLevelVOList.stream()
                .collect(
                        Collectors.toMap(WarningLevelVO::getWarningLevel,
                                WarningLevelVO::getLevelCount,
                                Long::sum));
        List<ResultWaringVO> resultWaringVOList = new ArrayList<>();
        //返回base下的所有告警等级以及对应的数量
        collect.forEach((k, v) -> {
            String warningLevelMeaning = "";
            for (LovValueDTO lovValueDTO : warningLevels) {
                if (k.equals(lovValueDTO.getValue())) {
                    warningLevelMeaning = lovValueDTO.getMeaning();
                }
            }
            ResultWaringVO resultWaringVO = ResultWaringVO.builder()
                    .warningLevel(warningLevelMeaning)
                    .countSum(v)
                    .build();
            resultWaringVOList.add(resultWaringVO);
        });
        batchResultBaseDTO.setResultWaringVOList(resultWaringVOList);
    }

    @Override
    public Long exec(Long tenantId, Long planId) {

        // 插入批数据方案结果表
        BatchResult batchResult = BatchResult.builder().planId(planId).startDate(new Date())
                .planStatus(PlanConstant.PlanStatus.RUNNING).tenantId(tenantId).build();
        batchResultRepository.insertSelective(batchResult);

        // 时间戳对象list
        List<TimestampControlDTO> timestampList = new ArrayList<>();

        try {
            // 规则计算
            ruleJudge(tenantId, planId, batchResult, timestampList);

            // 分数计算
            countScore(tenantId, batchResult);

            // 更新增量参数
            updateTimestamp(tenantId, timestampList, true);
        } catch (CommonException e) {
            // 更新增量参数
            updateTimestamp(tenantId, timestampList, false);
            batchResult.setPlanStatus(PlanConstant.PlanStatus.FAILED);
            batchResult.setExceptionInfo(MessageAccessor.getMessage(e.getMessage(), e.getParameters()).getDesc());
            batchResultRepository.updateByPrimaryKeySelective(batchResult);
            log.error("exec plan error!", e);
            throw e;
        } catch (Exception e) {
            // 更新增量参数
            updateTimestamp(tenantId, timestampList, false);
            batchResult.setPlanStatus(PlanConstant.PlanStatus.FAILED);
            batchResult.setExceptionInfo(ExceptionUtils.getMessage(e));
            batchResultRepository.updateByPrimaryKeySelective(batchResult);
            log.error("exec plan error!", e);
            throw e;
        }
        //质量评估完后。根据评估结果获取每个base的情况，去做血缘的质量问题的处理，此处理不阻塞且不影响评估流程
        ThreadPoolExecutor executor = CustomThreadPool.getExecutor();
        executor.submit(() -> qualityLineage(batchResult));
        return batchResult.getResultId();
    }

    private void qualityLineage(BatchResult batchResult) {
        try {
            //获取所有基础配置的结果
            List<BatchResultBase> batchResultBases = batchResultBaseRepository.select(
                    BatchResultBase.builder()
                            .resultId(batchResult.getResultId())
                            .tenantId(batchResult.getTenantId()).build()
            );
            //过滤出有异常的配置项
            List<BatchResultBase> errorBases = batchResultBases.stream().filter(batchResultBase -> batchResultBase.getExceptionRuleCount() > 0).collect(Collectors.toList());
            //过滤出正常的的配置项
            List<BatchResultBase> successBases = batchResultBases.stream().filter(batchResultBase -> batchResultBase.getExceptionRuleCount() == 0).collect(Collectors.toList());

            List<LineageDTO> lineageDTOS = new ArrayList<>();

            if (CollectionUtils.isNotEmpty(errorBases)) {
                errorBases.forEach(batchResultBase -> {
                    //获取基础配置
                    BatchPlanBase batchPlanBase = batchPlanBaseRepository.selectByPrimaryKey(batchResultBase.getPlanBaseId());
                    //构建有质量问题的血缘节点
                    LineageDTO lineageDTO = LineageDTO.builder()
                            .datasourceCode(batchPlanBase.getDatasourceCode())
                            .schemaName(batchPlanBase.getDatasourceSchema())
                            .tableName(batchPlanBase.getObjectName())
                            .qualityFlag(1)
                            .build();
                    lineageDTOS.add(lineageDTO);
                });
            }

            if (CollectionUtils.isNotEmpty(successBases)) {
                successBases.forEach(batchResultBase -> {
                    //获取基础配置
                    BatchPlanBase batchPlanBase = batchPlanBaseRepository.selectByPrimaryKey(batchResultBase.getPlanBaseId());
                    //构建有质量问题的血缘节点
                    LineageDTO lineageDTO = LineageDTO.builder()
                            .datasourceCode(batchPlanBase.getDatasourceCode())
                            .schemaName(batchPlanBase.getDatasourceSchema())
                            .tableName(batchPlanBase.getObjectName())
                            .qualityFlag(0)
                            .build();
                    lineageDTOS.add(lineageDTO);
                });
            }
            //修改血缘节点状态
            lineageFeign.updateLineageStatus(batchResult.getTenantId(), lineageDTOS);
        } catch (Exception e) {
            log.error("更新血缘异常！！！", e);
        }
    }

    /**
     * 更新增量参数
     *
     * @param tenantId      Long
     * @param timestampList List<TimestampControlDTO>
     * @param success       boolean
     */
    private void updateTimestamp(Long tenantId, List<TimestampControlDTO> timestampList, boolean success) {
        if (success) {
            for (TimestampControlDTO timestampControlDTO : timestampList) {
                ResponseEntity<String> response = timestampFeign.updateIncrement(tenantId, timestampControlDTO);
                ResponseUtils.getResponse(response, TimestampControlDTO.class);
            }
        } else {
            for (TimestampControlDTO timestampControlDTO : timestampList) {
                ResponseEntity<String> response = timestampFeign.updateIncrement(tenantId, TimestampControlDTO.builder()
                        .timestampType(timestampControlDTO.getTimestampType()).success(false).build());
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
    private void ruleJudge(Long tenantId, Long planId, BatchResult batchResult,
                           List<TimestampControlDTO> timestampList) {

        List<BatchPlanBase> baseList = batchPlanBaseRepository.select(BatchPlanBase.FIELD_PLAN_ID, planId);
        //评估每一个方案下的base
        for (BatchPlanBase batchPlanBase : baseList) {
            String objectName = batchPlanBase.getObjectName();


            PluginDatasourceDTO pluginDatasourceDTO = PluginDatasourceDTO.builder()
                    .datasourceId(batchPlanBase.getDatasourceId()).tenantId(tenantId)
                    .datasourceCode(batchPlanBase.getDatasourceCode()).build();


            String packageObjectName = objectName;
            if (PlanConstant.SqlType.SQL.equals(batchPlanBase.getSqlType())) {
                packageObjectName = String.format(SQL_PACK, objectName);
            }

            // 插入批数据方案结果表-表信息
            BatchResultBase batchResultBase = BatchResultBase.builder().resultId(batchResult.getResultId())
                    .planBaseId(batchPlanBase.getPlanBaseId()).objectName(objectName)
                    .packageObjectName(packageObjectName).datasourceType(batchPlanBase.getDatasourceType())
                    .ruleCount(0L).exceptionRuleCount(0L).checkItemCount(0L).exceptionCheckItemCount(0L)
                    .tenantId(tenantId).build();
            if (TABLE.equals(batchPlanBase.getSqlType())) {
                //保存表的数据量
                DriverSession driverSession = driverSessionService.getDriverSession(tenantId, batchPlanBase.getDatasourceCode());
                List<Map<String, Object>> maps = driverSession.executeOneQuery(batchPlanBase.getDatasourceSchema(),
                        String.format("select count(*) as COUNT from %s", batchPlanBase.getObjectName()));
                if (CollectionUtils.isNotEmpty(maps)) {
                    log.info("查询结果："+ maps);
                    String key = maps.get(0).keySet().iterator().next();
                    log.info("key:"+key);
                    Long count = Long.parseLong(String.valueOf(maps.get(0).get(key)));
                    batchResultBase.setDataCount(count);
                }
            }
            // 获取增量参数，并更新 where 条件
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
     * @param tenantId        Long
     * @param planId          Long
     * @param batchPlanBase   BatchPlanBase
     * @param batchResultBase BatchResultBase
     * @param timestampList   List<TimestampControlDTO>
     */
    private void updateWhereCondition(Long tenantId, Long planId, BatchPlanBase batchPlanBase,
                                      BatchResultBase batchResultBase, List<TimestampControlDTO> timestampList) {
        // 获取增量参数
        if (!PlanConstant.IncrementStrategy.NONE.equals(batchPlanBase.getIncrementStrategy())) {
            // 查询方案
            BatchPlan batchPlan = batchPlanRepository.selectByPrimaryKey(planId);
            String timestampType = String.format(PlanConstant.TIMESTAMP_TYPE, tenantId, batchPlan.getPlanCode(),
                    batchPlanBase.getPlanBaseId());
            ResponseEntity<String> incrementParam = timestampFeign.getIncrementParam(tenantId, timestampType);
            SpecifiedParamsResponseDO specifiedParamsResponseDO =
                    ResponseUtils.getResponse(incrementParam, SpecifiedParamsResponseDO.class);
            batchResultBase.setWhereCondition(ParamsUtil.handlePredefinedParams(batchPlanBase.getWhereCondition(),
                    specifiedParamsResponseDO));

            // 将增量参数添加到list中 全部处理完一起更新
            timestampList.add(TimestampControlDTO.builder().timestampType(timestampType)
                    .currentDateTime(specifiedParamsResponseDO.getCurrentDataTime())
                    .lastDateTime(specifiedParamsResponseDO.getLastDateTime())
                    .currentMaxId(specifiedParamsResponseDO.getCurrentMaxId())
                    .lastMaxId(specifiedParamsResponseDO.getLastMaxId()).success(true).build());
        }
    }


    /**
     * 处理表级规则
     *
     * @param tenantId            Long
     * @param batchResultBase     BatchResultBase
     * @param schema              String
     * @param pluginDatasourceDTO PluginDatasourceDTO
     */
    private void handleTableRule(Long tenantId, BatchResultBase batchResultBase, String schema,
                                 PluginDatasourceDTO pluginDatasourceDTO) {
        List<BatchPlanTable> tableList = batchPlanTableRepository.select(BatchPlanTable.FIELD_PLAN_BASE_ID,
                batchResultBase.getPlanBaseId());
        batchResultBase.setRuleCount(batchResultBase.getRuleCount() + tableList.size());

//        //表级所有异常消息
//        List<Map<String,Object>> tableExceptionList=new ArrayList<>();

        for (BatchPlanTable batchPlanTable : tableList) {
            List<BatchPlanTableConDO> conList = batchPlanTableConRepository.selectJoinItem(
                    BatchPlanTableConDO.builder().planRuleId(batchPlanTable.getPlanRuleId()).build());
            batchResultBase.setCheckItemCount(batchResultBase.getCheckItemCount() + conList.size());

            // 异常标记
            boolean exceptionFlag = false;

            BatchResultRuleDTO batchResultRuleDTO = BatchResultRuleDTO.builder()
                    .resultBaseId(batchResultBase.getResultBaseId()).ruleType(PlanConstant.ResultRuleType.TABLE)
                    .planRuleId(batchPlanTable.getPlanRuleId()).ruleCode(batchPlanTable.getRuleCode())
                    .ruleName(batchPlanTable.getRuleName()).ruleDesc(batchPlanTable.getRuleDesc())
                    .checkType(batchPlanTable.getCheckType()).weight(batchPlanTable.getWeight()).resultFlag(1)
                    .tenantId(tenantId).build();
            batchResultRuleRepository.insertDTOSelective(batchResultRuleDTO);

            List<BatchPlanTableLine> batchPlanTableLines = null;
            for (BatchPlanTableConDO batchPlanTableConDO : conList) {
                // 自定义SQL特殊转换
                if (PlanConstant.RuleType.SQL_CUSTOM.equals(batchPlanTable.getRuleType())) {
                    batchPlanTableConDO.setCheckItem(PlanConstant.RuleType.SQL_CUSTOM);
                    batchPlanTableLines = batchPlanTableLineRepository
                            .selectByCondition(Condition.builder(BatchPlanTableLine.class)
                                    .where(Sqls.custom().andEqualTo(
                                            BatchPlanTableLine.FIELD_PLAN_RULE_ID,
                                            batchPlanTable.getPlanRuleId()))
                                    .build());
                }
                Measure measure = measureCollector.getMeasure(batchPlanTableConDO.getCheckItem().toUpperCase());

                MeasureParamDO param = MeasureParamDO.builder().conditionId(batchPlanTableConDO.getConditionId())
                        .tenantId(tenantId).checkItem(batchPlanTableConDO.getCheckItem())
                        .countType(batchPlanTableConDO.getCountType())
                        .compareWay(batchPlanTableConDO.getCompareWay())
                        .whereCondition(joinWhereCondition(batchPlanTableConDO.getWhereCondition(),
                                batchResultBase.getWhereCondition()))
                        .warningLevelList(JsonUtils.json2WarningLevel(batchPlanTableConDO.getWarningLevel()))
                        .schema(schema).pluginDatasourceDTO(pluginDatasourceDTO)
                        .batchResultBase(batchResultBase).batchResultRuleDTO(batchResultRuleDTO)
                        .batchResultItem(BatchResultItem.builder().build()).build();
                if (batchPlanTableLines != null) {
                    param.setSql(batchPlanTableLines.get(0).getCustomSql());
                }
                measure.check(param);
//                param.setRuleName(batchPlanTable.getRuleName());
//                handleExceptionList(tableExceptionList,param);

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

                    // 异常阻断
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

//        //将所有表段级检验项异常数据存入es
//        String key=String.format("%s:%d",PlanConstant.CACHE_BUCKET_EXCEPTION,batchResultBase.getPlanBaseId());
//        redisTemplate.opsForHash().put(key, PlanConstant.ResultRuleType.TABLE,JsonUtils.object2Json(tableExceptionList));
    }


    /**
     * 处理字段规则
     *
     * @param tenantId            Long
     * @param batchResultBase     BatchResultBase
     * @param schema              String
     * @param pluginDatasourceDTO PluginDatasourceDTO
     */
    private void handleFieldRule(Long tenantId, BatchResultBase batchResultBase, String schema,
                                 PluginDatasourceDTO pluginDatasourceDTO) {
        List<BatchPlanField> fieldList = batchPlanFieldRepository.select(BatchPlanTable.FIELD_PLAN_BASE_ID,
                batchResultBase.getPlanBaseId());
        batchResultBase.setRuleCount(batchResultBase.getRuleCount() + fieldList.size());

        //字段所有异常消息
        List<Map<String, Object>> fieldExceptionList = new ArrayList<>();

        for (BatchPlanField batchPlanField : fieldList) {
            List<BatchPlanFieldConDO> conList = batchPlanFieldConRepository.selectJoinItem(
                    BatchPlanFieldConDO.builder().planRuleId(batchPlanField.getPlanRuleId()).build());
            batchResultBase.setCheckItemCount(batchResultBase.getCheckItemCount() + conList.size());

            // 异常标记
            boolean exceptionFlag = false;
            BatchResultRuleDTO batchResultRuleDTO = BatchResultRuleDTO.builder()
                    .resultBaseId(batchResultBase.getResultBaseId()).ruleType(PlanConstant.ResultRuleType.FIELD)
                    .planRuleId(batchPlanField.getPlanRuleId()).ruleCode(batchPlanField.getRuleCode())
                    .ruleName(batchPlanField.getRuleName()).ruleDesc(batchPlanField.getRuleDesc())
                    .checkType(batchPlanField.getCheckType()).weight(batchPlanField.getWeight()).resultFlag(1)
                    .tenantId(tenantId).build();
            batchResultRuleRepository.insertDTOSelective(batchResultRuleDTO);

            for (BatchPlanFieldConDO batchPlanFieldConDO : conList) {

                Measure measure;
                if (PlanConstant.CheckWay.COMMON.equals(batchPlanFieldConDO.getCheckWay())) {
                    measure = measureCollector.getMeasure(batchPlanFieldConDO.getCheckItem().toUpperCase());
                } else {
                    measure = measureCollector.getMeasure(PlanConstant.CheckWay.REGULAR);
                }

                MeasureParamDO param = MeasureParamDO.builder().tenantId(tenantId)
                        .conditionId(batchPlanFieldConDO.getConditionId())
                        .checkItem(batchPlanFieldConDO.getCheckItem())
                        .countType(batchPlanFieldConDO.getCountType())
                        .compareWay(batchPlanFieldConDO.getCompareWay())
                        .whereCondition(joinWhereCondition(batchPlanFieldConDO.getWhereCondition(),
                                batchResultBase.getWhereCondition()))
                        .warningLevelList(JsonUtils.json2WarningLevel(batchPlanFieldConDO.getWarningLevel()))
                        .schema(schema).pluginDatasourceDTO(pluginDatasourceDTO)
                        .fieldName(batchPlanFieldConDO.getFieldName())
                        .checkFieldName(batchPlanFieldConDO.getCheckFieldName())
                        .regularExpression(batchPlanFieldConDO.getRegularExpression())
                        .batchResultBase(batchResultBase).batchResultRuleDTO(batchResultRuleDTO)
                        .batchResultItem(BatchResultItem.builder().build()).build();
                measure.check(param);
                param.setRuleName(batchPlanField.getRuleName());
                handleExceptionList(fieldExceptionList, param);

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

                if (StringUtils.isNotBlank(batchResultItem.getWarningLevel()) && !"[]".equals(batchResultItem.getWarningLevel())) {
                    batchResultBase.setExceptionCheckItemCount(batchResultBase.getExceptionCheckItemCount() + 1);
                    exceptionFlag = true;

                    // 异常阻断
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

        //将所有字段级检验项异常数据存入redis
//        String key = String.format("%s:%d", PlanConstant.CACHE_BUCKET_EXCEPTION, batchResultBase.getPlanBaseId());
//        redisTemplate.opsForHash().put(key, PlanConstant.ResultRuleType.FIELD, JsonUtils.object2Json(fieldExceptionList));

        //将所有字段级校验项异常数据存入数据库
        String exceptionList = String.format("{\"FIELD\":%s}", JsonUtils.object2Json(fieldExceptionList));
        batchResultBase.setExceptionList(exceptionList);
        batchResultBaseRepository.updateByPrimaryKey(batchResultBase);
    }

    private void handleExceptionList(List<Map<String, Object>> fieldExceptionList, MeasureParamDO param) {
        boolean flag;
        if (CollectionUtils.isNotEmpty(param.getExceptionMapList())) {
            if (CollectionUtils.isEmpty(fieldExceptionList)) {
                //为空直接合并
                CollectionUtils.addAll(fieldExceptionList, param.getExceptionMapList());
            } else {
                //不为空
                for (Map<String, Object> map : param.getExceptionMapList()) {
                    flag = false;
                    String pk = String.valueOf(map.get(ExceptionParam.PK));
                    for (Map<String, Object> fieldMap : fieldExceptionList) {
                        String fieldPk = String.valueOf(fieldMap.get(ExceptionParam.PK));
                        //判断如果异常数据已存在，合并异常信息
                        if (fieldPk.equals(pk)) {
                            String newExceptionInfo = String.valueOf(map.get(ExceptionParam.EXCEPTION_INFO));
                            String exceptionInfo = String.valueOf(fieldMap.get(ExceptionParam.EXCEPTION_INFO));

                            String newWarningLevel = String.valueOf(map.get(ExceptionParam.WARNING_LEVEL));
                            String warningLevel = String.valueOf(fieldMap.get(ExceptionParam.WARNING_LEVEL));
                            fieldMap.put(ExceptionParam.EXCEPTION_INFO, String.format("%s,%s", exceptionInfo, newExceptionInfo));
                            List<String> warningLevelList = Arrays.asList(warningLevel.split(","));
                            //不包含这个异常等级
                            if (CollectionUtils.isEmpty(warningLevelList) || !warningLevelList.contains(newWarningLevel)) {
                                fieldMap.put(ExceptionParam.WARNING_LEVEL, String.format("%s,%s", warningLevel, newWarningLevel));
                            }
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        fieldExceptionList.add(map);
                    }
                }

            }
        }
    }

    /**
     * 处理表间规则
     *
     * @param tenantId        tenantId
     * @param batchResultBase batchResultBase
     * @param schema          String
     */
    private void handleRelTableRule(Long tenantId, BatchResultBase batchResultBase, String schema,
                                    PluginDatasourceDTO pluginDatasourceDTO) {

        List<BatchPlanRelTable> relTableList = batchPlanRelTableRepository.select(BatchPlanTable.FIELD_PLAN_BASE_ID,
                batchResultBase.getPlanBaseId());
        batchResultBase.setRuleCount(batchResultBase.getRuleCount() + relTableList.size());
        batchResultBase.setCheckItemCount(batchResultBase.getCheckItemCount() + relTableList.size());

        //表间所有异常消息
//        List<Map<String,Object>> relExceptionList=new ArrayList<>();

        for (BatchPlanRelTable batchPlanRelTable : relTableList) {
            Measure measure = measureCollector.getMeasure(PlanConstant.RuleType.TABLE_RELATION);

            MeasureParamDO param = MeasureParamDO.builder().checkItem(batchPlanRelTable.getCheckItem())
                    .tenantId(tenantId).schema(schema).pluginDatasourceDTO(pluginDatasourceDTO)
                    .batchPlanRelTable(batchPlanRelTable).batchResultBase(batchResultBase)
                    .batchResultItem(BatchResultItem.builder().build()).build();
            measure.check(param);
//            param.setRuleName(batchPlanRelTable.getRuleName());
//            handleExceptionList(relExceptionList,param);

            BatchResultRuleDTO batchResultRuleDTO = BatchResultRuleDTO.builder()
                    .resultBaseId(batchResultBase.getResultBaseId())
                    .ruleType(PlanConstant.ResultRuleType.REL_TABLE)
                    .planRuleId(batchPlanRelTable.getPlanRuleId()).ruleCode(batchPlanRelTable.getRuleCode())
                    .ruleName(batchPlanRelTable.getRuleName()).ruleDesc(batchPlanRelTable.getRuleDesc())
                    .checkType(batchPlanRelTable.getCheckType()).weight(batchPlanRelTable.getWeight())
                    .resultFlag(1).tenantId(tenantId).build();

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
            List<TableRelCheckDTO> tableRelCheckDTOS =
                    JsonUtils.json2TableRelCheck(batchPlanRelTable.getTableRelCheck());
            List<String> resultList = new ArrayList<>();
            for (TableRelCheckDTO tableRelCheckDTO : tableRelCheckDTOS) {
                String result = String.format("%s%s=%s%s", tableRelCheckDTO.getRelFunction(),
                        Objects.nonNull(tableRelCheckDTO.getRelFunction())
                                ? String.format("(%s)", tableRelCheckDTO.getRelFieldName())
                                : tableRelCheckDTO.getRelFieldName(),
                        tableRelCheckDTO.getBaseFunction(),
                        Objects.nonNull(tableRelCheckDTO.getBaseFunction())
                                ? String.format("(%s)", tableRelCheckDTO.getBaseFieldName())
                                : tableRelCheckDTO.getBaseFieldName());
                resultList.add(result);
            }
            batchResultItem.setActualValue(Strings.join(resultList, ','));
            batchResultItemRepository.insertSelective(batchResultItem);

            if (StringUtils.isNotBlank(batchResultItem.getWarningLevel())) {
                batchResultRuleDTO.setResultFlag(0);
                batchResultRuleRepository.updateByDTOPrimaryKeySelective(batchResultRuleDTO);

                batchResultBase.setExceptionRuleCount(batchResultBase.getExceptionRuleCount() + 1);
                batchResultBase.setExceptionCheckItemCount(batchResultBase.getExceptionCheckItemCount() + 1);

                // 异常阻断
                if (batchPlanRelTable.getExceptionBlock() == 1) {
                    batchResultBaseRepository.updateByPrimaryKeySelective(batchResultBase);
                    throw new CommonException(ErrorCode.EXCEPTION_BLOCK);
                }
            }
        }

        //将所有表间段级检验项异常数据存入es
//        String key=String.format("%s:%d",PlanConstant.CACHE_BUCKET_EXCEPTION,batchResultBase.getPlanBaseId());
//        redisTemplate.opsForHash().put(key, PlanConstant.ResultRuleType.REL_TABLE,JsonUtils.object2Json(relExceptionList));
    }

    /**
     * 分数计算
     *
     * @param tenantId    tenantId
     * @param batchResult 结果表对象
     */
    private void countScore(Long tenantId, BatchResult batchResult) {

        // 获取所有告警等级，计算 F 值
        List<LovValueDTO> list = lovAdapter.queryLovValue(PlanConstant.LOV_WARNING_LEVEL, tenantId);
        Map<String, BigDecimal> map = new HashMap<>(8);
        BigDecimal n = BigDecimal.valueOf(list.size());
        BigDecimal half = BigDecimal.valueOf(0.5);
        BigDecimal f = BigDecimal.ONE.divide(n, 10, RoundingMode.HALF_UP).multiply(half);
        for (LovValueDTO lovValueDTO : list) {
            map.put(lovValueDTO.getValue(), f);
            f = f.add(BigDecimal.ONE.divide(n, 10, RoundingMode.HALF_UP));
        }
        // 正常规则F 为1
        map.put(PlanConstant.WARNING_LEVEL_NORMAL, BigDecimal.ONE);

        // 查询规则
        List<BatchResultItemDTO> itemDTOList = batchResultItemRepository.selectByResultId(batchResult.getResultId());
        BigDecimal w = BigDecimal.valueOf(itemDTOList.stream().mapToLong(BatchResultItemDTO::getWeight).sum());
        BigDecimal sum = BigDecimal.ZERO;
        for (BatchResultItemDTO batchResultItemDTO : itemDTOList) {
            // 一个校验项满足多个告警
            if (Strings.isNotEmpty(batchResultItemDTO.getWarningLevel())
                    && !"[]".equals(batchResultItemDTO.getWarningLevel())
                    && !PlanConstant.WARNING_LEVEL_NORMAL.equals(batchResultItemDTO.getWarningLevel())) {
                List<WarningLevelVO> warningLevelVOS =
                        JsonUtils.json2WarningLevelVO(batchResultItemDTO.getWarningLevel());
                for (WarningLevelVO warningLevelVO : warningLevelVOS) {
                    BigDecimal multiply = BigDecimal.valueOf(batchResultItemDTO.getWeight())
                            .divide(w, 10, RoundingMode.HALF_UP)
                            .multiply(map.get(warningLevelVO.getWarningLevel()));
                    sum = sum.add(multiply);
                }
            } else {
                BigDecimal multiply =
                        BigDecimal.valueOf(batchResultItemDTO.getWeight()).divide(w, 10, RoundingMode.HALF_UP)
                                .multiply(map.get(PlanConstant.WARNING_LEVEL_NORMAL));
                sum = sum.add(multiply);
            }
        }
        batchResult.setMark(sum.multiply(BigDecimal.valueOf(100)));
        batchResult.setPlanStatus(PlanConstant.PlanStatus.SUCCESS);
        batchResult.setEndDate(new Date());
        batchResultRepository.updateByPrimaryKeySelective(batchResult);
    }


    /**
     * 拼接where条件
     *
     * @param where1 String
     * @param where2 String
     * @return String
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
}
