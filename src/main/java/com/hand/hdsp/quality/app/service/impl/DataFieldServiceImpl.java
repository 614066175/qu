package com.hand.hdsp.quality.app.service.impl;


import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.DataFieldService;
import com.hand.hdsp.quality.app.service.DataStandardService;
import com.hand.hdsp.quality.domain.entity.*;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.WorkFlowConstant;
import com.hand.hdsp.quality.infra.mapper.DataFieldMapper;
import com.hand.hdsp.quality.infra.mapper.DataStandardMapper;
import com.hand.hdsp.quality.infra.statistic.validator.StatisticValidator;
import com.hand.hdsp.quality.infra.util.CustomThreadPool;
import com.hand.hdsp.quality.infra.util.StandardHandler;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.boot.workflow.WorkflowClient;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.driver.core.session.DriverSession;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static com.hand.hdsp.quality.infra.constant.PlanConstant.CheckType.STANDARD;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.FIELD;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.Status.*;

/**
 * <p>字段标准表应用服务默认实现</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
@Slf4j
@Service
public class DataFieldServiceImpl implements DataFieldService {

    public static final Long DEFAULT_VERSION = 1L;

    private final DataFieldRepository dataFieldRepository;

    private final StandardExtraRepository standardExtraRepository;

    private final StandardApproveRepository standardApproveRepository;

    private final DataFieldVersionRepository dataFieldVersionRepository;

    private final DataFieldMapper dataFieldMapper;

    private final DataStandardService dataStandardService;

    private final StandardAimRepository standardAimRepository;

    private final ExtraVersionRepository extraVersionRepository;

    private final DataStandardMapper dataStandardMapper;

    @Value("${hdsp.workflow.enabled:false}")
    private boolean enableWorkflow;

    @Autowired
    private WorkflowClient workflowClient;

    private final DriverSessionService driverSessionService;


    private final AimStatisticsRepository aimStatisticsRepository;

    private final StandardTeamRepository standardTeamRepository;

    private final StandardRelationRepository standardRelationRepository;

    @Autowired
    private List<StandardHandler> handlers;

    private final List<StatisticValidator> statisticValidatorList;

    //统计行数
    private final static String COUNT_SQL = "SELECT COUNT(1) as COUNT FROM %s";

    //统计非空行数
    private final static String NOT_NULL_COUNT = "SELECT COUNT(*)  FROM %s where %s is not null";

    public DataFieldServiceImpl(DataFieldRepository dataFieldRepository, StandardExtraRepository standardExtraRepository,
                                StandardApproveRepository standardApproveRepository, DataFieldVersionRepository dataFieldVersionRepository,
                                DataFieldMapper dataFieldMapper, DataStandardService dataStandardService, ExtraVersionRepository extraVersionRepository,
                                DataStandardMapper dataStandardMapper, StandardAimRepository standardAimRepository,
                                StandardTeamRepository standardTeamRepository, StandardRelationRepository standardRelationRepository,
                                AimStatisticsRepository aimStatisticsRepository, List<StatisticValidator> statisticValidatorList,
                                DriverSessionService driverSessionService) {
        this.dataFieldRepository = dataFieldRepository;
        this.standardExtraRepository = standardExtraRepository;
        this.standardApproveRepository = standardApproveRepository;
        this.dataFieldVersionRepository = dataFieldVersionRepository;
        this.dataFieldMapper = dataFieldMapper;
        this.dataStandardService = dataStandardService;
        this.standardAimRepository = standardAimRepository;
        this.extraVersionRepository = extraVersionRepository;
        this.dataStandardMapper = dataStandardMapper;
        this.standardTeamRepository = standardTeamRepository;
        this.standardRelationRepository = standardRelationRepository;
        this.driverSessionService = driverSessionService;
        this.aimStatisticsRepository = aimStatisticsRepository;
        this.statisticValidatorList = statisticValidatorList;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DataFieldDTO dataFieldDTO) {
        List<DataFieldDTO> dataFieldDTOS = dataFieldRepository.selectDTOByCondition(Condition.builder(DataField.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataField.FIELD_FIELD_NAME, dataFieldDTO.getFieldName())
                        .andEqualTo(DataField.FIELD_TENANT_ID, dataFieldDTO.getTenantId())
                        .andEqualTo(DataField.FIELD_PROJECT_ID, dataFieldDTO.getProjectId()))
                .build());
        if (CollectionUtils.isNotEmpty(dataFieldDTOS)) {
            throw new CommonException(ErrorCode.DATA_FIELD_NAME_EXIST);
        }

        dataFieldDTO.setStandardStatus(CREATE);
        dataFieldRepository.insertDTOSelective(dataFieldDTO);

        //如果新建的时候维护了标准组，则维护此标准与标准组的关系
        if (CollectionUtils.isNotEmpty(dataFieldDTO.getStandardTeamDTOList())) {
            List<StandardTeamDTO> standardTeamDTOList = dataFieldDTO.getStandardTeamDTOList();
            List<StandardRelationDTO> standardRelationDTOList = new ArrayList<>();
            standardTeamDTOList.forEach(standardTeamDTO ->
                    standardRelationDTOList.add(StandardRelationDTO.builder().fieldStandardId(dataFieldDTO.getFieldId())
                            .standardTeamId(standardTeamDTO.getStandardTeamId())
                            .build()));
            standardRelationRepository.batchInsertDTOSelective(standardRelationDTOList);
        }

        List<StandardExtraDTO> standardExtraDTOList = dataFieldDTO.getStandardExtraDTOList();
        if (CollectionUtils.isNotEmpty(standardExtraDTOList)) {
            standardExtraDTOList.forEach(s -> {
                StandardExtraDTO extraDTO = StandardExtraDTO.builder()
                        .standardId(dataFieldDTO.getFieldId())
                        .extraKey(s.getExtraKey())
                        .extraValue(s.getExtraValue())
                        .standardType(FIELD)
                        .tenantId(dataFieldDTO.getTenantId())
                        .projectId(dataFieldDTO.getProjectId())
                        .build();
                standardExtraRepository.insertDTOSelective(extraDTO);
            });
        }
    }

    @Override
    public DataFieldDTO detail(Long tenantId, Long fieldId) {
        List<DataFieldDTO> dataFieldDTOList = dataFieldMapper.list(DataFieldDTO
                .builder()
                .fieldId(fieldId)
                .build());
        if (CollectionUtils.isEmpty(dataFieldDTOList)) {
            throw new CommonException(ErrorCode.DATA_FIELD_STANDARD_NOT_EXIST);
        }
        DataFieldDTO dataFieldDTO = dataFieldDTOList.get(0);
        //判断当前租户是否启用安全加密
        if (dataStandardMapper.isEncrypt(tenantId) == 1) {
            //解密邮箱，电话
            if (Strings.isNotEmpty(dataFieldDTO.getChargeTel())) {
                dataFieldDTO.setChargeTel(DataSecurityHelper.decrypt(dataFieldDTO.getChargeTel()));
            }
            if (Strings.isNotEmpty(dataFieldDTO.getChargeEmail())) {
                dataFieldDTO.setChargeEmail(DataSecurityHelper.decrypt(dataFieldDTO.getChargeEmail()));
            }
            if (Strings.isNotEmpty(dataFieldDTO.getChargeDeptName())) {
                dataFieldDTO.setChargeDeptName(DataSecurityHelper.decrypt(dataFieldDTO.getChargeDeptName()));
            }
        }
        List<StandardExtraDTO> standardExtraDTOS = standardExtraRepository.selectDTOByCondition(Condition.builder(StandardExtra.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardExtra.FIELD_STANDARD_ID, fieldId)
                        .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE, FIELD)
                        .andEqualTo(StandardExtra.FIELD_TENANT_ID, tenantId))
                .build());
        dataFieldDTO.setStandardExtraDTOList(standardExtraDTOS);
        //查询标准组
        List<StandardRelation> standardRelations = standardRelationRepository.select(StandardRelation.builder().fieldStandardId(fieldId).build());
        List<Long> standardTeamIds = standardRelations.stream()
                .map(StandardRelation::getStandardTeamId)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(standardTeamIds)) {
            List<StandardTeamDTO> standardTeamDTOS = standardTeamRepository.selectDTOByIds(standardTeamIds);
            dataFieldDTO.setStandardTeamDTOList(standardTeamDTOS);
        }

        return dataFieldDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(DataFieldDTO dataFieldDTO) {
        if (ONLINE.equals(dataFieldDTO.getStandardStatus())
                || OFFLINE_APPROVING.equals(dataFieldDTO.getStandardStatus())) {
            throw new CommonException(ErrorCode.DATA_FIELD_CAN_NOT_DELETE);
        }
        dataFieldRepository.deleteByPrimaryKey(dataFieldDTO);
        //删除版本表数据
        List<DataFieldVersionDTO> dataStandardVersionDTOS = dataFieldVersionRepository.selectDTOByCondition(Condition.builder(DataFieldVersion.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataFieldVersion.FIELD_FIELD_ID, dataFieldDTO.getFieldId())
                        .andEqualTo(DataFieldVersion.FIELD_TENANT_ID, dataFieldDTO.getTenantId())
                        .andEqualTo(DataFieldVersion.FIELD_PROJECT_ID, dataFieldDTO.getProjectId()))
                .build());
        dataFieldVersionRepository.batchDTODeleteByPrimaryKey(dataStandardVersionDTOS);
        // 删除额外信息
        List<StandardExtraDTO> standardExtraDTOS = standardExtraRepository.selectDTOByCondition(Condition.builder(StandardExtra.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardExtra.FIELD_STANDARD_ID, dataFieldDTO.getFieldId())
                        .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE, FIELD)
                        .andEqualTo(StandardExtra.FIELD_TENANT_ID, dataFieldDTO.getTenantId())
                        .andEqualTo(StandardExtra.FIELD_PROJECT_ID, dataFieldDTO.getProjectId())
                ).build());
        //todo 删除落标表
        standardExtraRepository.batchDTODeleteByPrimaryKey(standardExtraDTOS);

        //删除与标准组关系
        List<StandardRelation> standardRelationList = standardRelationRepository
                .select(StandardRelation.builder().fieldStandardId(dataFieldDTO.getFieldId()).build());
        if (CollectionUtils.isNotEmpty(standardRelationList)) {
            standardRelationRepository.batchDeleteByPrimaryKey(standardRelationList);
        }
    }

    @Override
    public Page<DataFieldDTO> list(PageRequest pageRequest, DataFieldDTO dataFieldDTO) {
        return PageHelper.doPageAndSort(pageRequest, () -> dataFieldMapper.list(dataFieldDTO));
    }

    @Override
    public void updateStatus(DataFieldDTO dataFieldDTO) {
        DataFieldDTO oldDataFieldDTO = dataFieldRepository.selectDTOByPrimaryKey(dataFieldDTO.getFieldId());
        if (Objects.isNull(oldDataFieldDTO)) {
            throw new CommonException(ErrorCode.DATA_FIELD_NAME_EXIST);
        }
        oldDataFieldDTO.setStandardStatus(dataFieldDTO.getStandardStatus());
        dataFieldRepository.updateByDTOPrimaryKey(oldDataFieldDTO);
    }

    @Override
    public void aim(Long tenantId, List<StandardAimDTO> standardAimDTOList, Long projectId) {
        dataStandardService.aim(tenantId, standardAimDTOList, projectId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishOrOff(DataFieldDTO dataFieldDTO) {
        if (enableWorkflow) {
            //开启工作流
            //根据上下线状态开启不同的工作流实例
            if (ONLINE.equals(dataFieldDTO.getStandardStatus())) {
                //先修改状态再启动工作流，启动工作流需要花费一定时间,有异常回滚
                this.workflowing(dataFieldDTO.getTenantId(), dataFieldDTO.getFieldId(), ONLINE_APPROVING);
                this.startWorkFlow(WorkFlowConstant.FieldStandard.ONLINE_WORKFLOW_KEY, dataFieldDTO);
            }
            if (OFFLINE.equals(dataFieldDTO.getStandardStatus())) {
                this.workflowing(dataFieldDTO.getTenantId(), dataFieldDTO.getFieldId(), OFFLINE_APPROVING);
                this.startWorkFlow(WorkFlowConstant.FieldStandard.OFFLINE_WORKFLOW_KEY, dataFieldDTO);
            }
        } else {
            DataFieldDTO dto = dataFieldRepository.selectDTOByPrimaryKey(dataFieldDTO.getFieldId());
            if (Objects.isNull(dto)) {
                throw new CommonException(ErrorCode.DATA_FIELD_STANDARD_NOT_EXIST);
            }
            dataFieldDTO.setObjectVersionNumber(dto.getObjectVersionNumber());
            dataFieldRepository.updateDTOOptional(dataFieldDTO, DataField.FIELD_STANDARD_STATUS);
            if (ONLINE.equals(dataFieldDTO.getStandardStatus())) {
                //存版本表
                doVersion(dataFieldDTO);
            }
        }
    }

    private void startWorkFlow(String workflowKey, DataFieldDTO dataFieldDTO) {
        //使用当前时间戳作为业务主键
        String bussinessKey = String.valueOf(System.currentTimeMillis());
        Map<String, Object> var = new HashMap<>();
        //给流程变量
        var.put("fieldId", dataFieldDTO.getFieldId());
        //使用自研工作流客户端
        workflowClient.startInstanceByFlowKey(dataFieldDTO.getTenantId(), workflowKey, bussinessKey, "USER", String.valueOf(DetailsHelper.getUserDetails().getUserId()), var);
    }


    @Override
    public List<DataFieldDTO> export(DataFieldDTO dto, ExportParam exportParam, PageRequest pageRequest) {
        Page<DataFieldDTO> list = list(pageRequest, dto);
        if (list == null) {
            return new ArrayList<>();
        }
        for (DataFieldDTO dataFieldDTO : list) {
            // 如果开启了数据加密
            if (dataStandardMapper.isEncrypt(dataFieldDTO.getTenantId()) == 1) {
                decodeForDataFieldDTO(dataFieldDTO);
            }
        }
        return list;
    }

    /**
     * 保证导入导出数据的一致性
     *
     * @param dataFieldDTO
     */
    public void decodeForDataFieldDTO(DataFieldDTO dataFieldDTO) {
        // 解密电话号码
        if (StringUtils.isNotEmpty(dataFieldDTO.getChargeTel())) {
            dataFieldDTO.setChargeTel(DataSecurityHelper.decrypt(dataFieldDTO.getChargeTel()));
        }
        // 解密邮箱地址
        if (StringUtils.isNotEmpty(dataFieldDTO.getChargeEmail())) {
            dataFieldDTO.setChargeEmail(DataSecurityHelper.decrypt(dataFieldDTO.getChargeEmail()));
        }
        // 解密部门名称
        if (StringUtils.isNotEmpty(dataFieldDTO.getChargeDeptName())) {
            dataFieldDTO.setChargeDeptName(DataSecurityHelper.decrypt(dataFieldDTO.getChargeDeptName()));
        }
    }

    private void doVersion(DataFieldDTO dataFieldDTO) {
        Long lastVersion = DEFAULT_VERSION;
        List<DataFieldVersionDTO> dataFieldVersionDTOS = dataFieldVersionRepository.selectDTOByCondition(Condition.builder(DataFieldVersion.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataField.FIELD_FIELD_ID, dataFieldDTO.getFieldId()))
                .orderByDesc(DataFieldVersion.FIELD_VERSION_NUMBER).build());
        DataFieldVersionDTO dataFieldVersionDTO = new DataFieldVersionDTO();
        //不为空则取最新版本
        if (CollectionUtils.isNotEmpty(dataFieldVersionDTOS)) {
            lastVersion = dataFieldVersionDTOS.get(0).getVersionNumber() + 1;
        }
        //存入版本表
        BeanUtils.copyProperties(dataFieldDTO, dataFieldVersionDTO);
        dataFieldVersionDTO.setVersionNumber(lastVersion);
        dataFieldVersionRepository.insertDTOSelective(dataFieldVersionDTO);
        //存附加信息版本表
        List<StandardExtraDTO> standardExtraDTOS = standardExtraRepository.selectDTOByCondition(Condition.builder(StandardExtra.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardExtra.FIELD_STANDARD_ID, dataFieldDTO.getFieldId())
                        .andEqualTo(StandardExtra.FIELD_STANDARD_TYPE, FIELD)
                        .andEqualTo(StandardExtra.FIELD_TENANT_ID, dataFieldDTO.getTenantId())
                        .andEqualTo(StandardExtra.FIELD_PROJECT_ID, dataFieldDTO.getProjectId()))
                .build());
        if (CollectionUtils.isNotEmpty(standardExtraDTOS)) {
            //存附件信息版本表
            for (StandardExtraDTO s : standardExtraDTOS) {
                ExtraVersionDTO extraVersionDTO = new ExtraVersionDTO();
                BeanUtils.copyProperties(s, extraVersionDTO);
                extraVersionDTO.setVersionNumber(lastVersion);
                extraVersionRepository.insertDTOSelective(extraVersionDTO);
            }
        }
    }

    @Override
    public void onlineWorkflowSuccess(Long tenantId, Long fieldId) {
        workflowing(tenantId, fieldId, ONLINE);
    }

    @Override
    public void onlineWorkflowFail(Long tenantId, Long fieldId) {
        workflowing(tenantId, fieldId, OFFLINE);
    }

    @Override
    public void offlineWorkflowSuccess(Long tenantId, Long fieldId) {
        workflowing(tenantId, fieldId, OFFLINE);
    }

    @Override
    public void offlineWorkflowFail(Long tenantId, Long fieldId) {
        workflowing(tenantId, fieldId, ONLINE);
    }

    @Override
    public void onlineWorkflowing(Long tenantId, Long fieldId) {
        workflowing(tenantId, fieldId, ONLINE_APPROVING);
    }

    @Override
    public void offlineWorkflowing(Long tenantId, Long fieldId) {
        workflowing(tenantId, fieldId, OFFLINE_APPROVING);
    }

    @Override
    public List<AssigneeUserDTO> findCharger(Long tenantId, Long fieldId) {
        DataFieldDTO dataFieldDTO = dataFieldRepository.selectDTOByPrimaryKey(
                DataFieldDTO.builder().fieldId(fieldId).tenantId(tenantId).build()
        );
        if (dataFieldDTO != null) {
            //查询责任人
            return Arrays.asList(dataStandardMapper.selectAssigneeUser(dataFieldDTO.getChargeId()));
        } else {
            throw new CommonException(ErrorCode.NOT_FIND_VALUE);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataFieldDTO update(DataFieldDTO dataFieldDTO) {
        //更新
        dataFieldRepository.updateByDTOPrimaryKey(dataFieldDTO);

        //更新标准组关系
        List<StandardRelation> standardRelations = standardRelationRepository.select(StandardRelation.builder().fieldStandardId(dataFieldDTO.getFieldId()).build());
        if (CollectionUtils.isNotEmpty(standardRelations)) {
            //删除旧的
            standardRelationRepository.batchDeleteByPrimaryKey(standardRelations);
        }
        //维护新的关系
        if (CollectionUtils.isNotEmpty(dataFieldDTO.getStandardTeamDTOList())) {
            List<StandardTeamDTO> standardTeamDTOList = dataFieldDTO.getStandardTeamDTOList();
            List<StandardRelationDTO> standardRelationDTOList = new ArrayList<>();
            standardTeamDTOList.forEach(standardTeamDTO ->
                    standardRelationDTOList.add(StandardRelationDTO.builder().fieldStandardId(dataFieldDTO.getFieldId())
                            .standardTeamId(standardTeamDTO.getStandardTeamId())
                            .build()));
            standardRelationRepository.batchInsertDTOSelective(standardRelationDTOList);
        }
        return dataFieldDTO;
    }

    @Override
    public BatchPlanFieldDTO standardToRule(Long standardId, String fieldType) {
        DataFieldDTO dataFieldDTO = dataFieldRepository.selectDTOByPrimaryKey(standardId);
        BatchPlanFieldDTO batchPlanFieldDTO = BatchPlanFieldDTO.builder()
                .ruleCode(dataFieldDTO.getFieldName() + "_auto")
                .ruleName(dataFieldDTO.getFieldComment() + "自生成规则")
                .ruleDesc(dataFieldDTO.getStandardDesc())
                .checkType(STANDARD)
                .autoBuildFlag(1)
                .weight(5L)
                .build();
        List<BatchPlanFieldLineDTO> batchPlanFieldLineDTOList = new ArrayList<>();
        handlers.forEach(standardHandler -> {
            BatchPlanFieldLineDTO fieldLineDTO = standardHandler.handle(dataFieldDTO, fieldType);
            if (fieldLineDTO != null) {
                batchPlanFieldLineDTOList.add(fieldLineDTO);
            }
        });
        batchPlanFieldDTO.setBatchPlanFieldLineDTOList(batchPlanFieldLineDTOList);
        return batchPlanFieldDTO;
    }


    @Override
    public DataFieldDTO fieldAimStatistic(DataFieldDTO dataFieldDTO) {
        //根据字段标准，查找对应落标记录
        List<StandardAimDTO> standardAimDTOS = standardAimRepository.selectDTOByCondition(Condition.builder(StandardAim.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardAim.FIELD_STANDARD_TYPE, FIELD)
                        .andEqualTo(StandardAim.FIELD_STANDARD_ID, dataFieldDTO.getFieldId())
                        .andEqualTo(StandardAim.FIELD_TENANT_ID, dataFieldDTO.getTenantId()))
                .build());
        if (CollectionUtils.isEmpty(standardAimDTOS)) {
            //如果表没有落标记录
            log.info("字段标准没有落标，无需统计");
            throw new CommonException(ErrorCode.STANDARD_NO_AIM);
        }

        List<AimStatisticsDTO> aimStatisticsDTOS = new CopyOnWriteArrayList<>();
        AimStatisticsDTO aimStatisticsDTO = new AimStatisticsDTO();

        CountDownLatch countDownLatch = new CountDownLatch(standardAimDTOS.size());
        for (StandardAimDTO aimDTO : standardAimDTOS) {
            CustomThreadPool.getExecutor().submit(() -> {
                try {
                    aimStatisticsDTO.setAimId(aimDTO.getAimId());
                    String fieldName = aimDTO.getFieldName().split("\\(")[0];
                    String dataSourceCode = aimDTO.getDatasourceCode();
                    String schemaName = aimDTO.getSchemaName();
                    String tableName = aimDTO.getTableName();

                    DriverSession driverSession = driverSessionService.getDriverSession(aimDTO.getTenantId(), dataSourceCode);
                    List<Map<String, Object>> countResult = driverSession.executeOneQuery(schemaName, String.format(COUNT_SQL, tableName));
                    if (CollectionUtils.isNotEmpty(countResult)) {
                        log.info("行数查询结果【{}】", countResult);
                        String count = countResult.get(0).values().toArray()[0].toString();
                        aimStatisticsDTO.setRowNum(Long.parseLong(count));
                    }

                    List<Map<String, Object>> notNullResult = driverSession.executeOneQuery(schemaName, String.format(NOT_NULL_COUNT, tableName, fieldName));
                    if (CollectionUtils.isNotEmpty(notNullResult)) {
                        log.info("非空行数查询结果【{}】", notNullResult);
                        String notNullCount = countResult.get(0).values().toArray()[0].toString();
                        aimStatisticsDTO.setNonNullRow(Long.parseLong(notNullCount));
                    }

                    // 统计合规行数 字段类型校验，字段长度、字段精度、数据格式、值域类型校验（若字段标准中维护了值）
                    //1.查询出字段标准表，并取出对应的合规校验 字段
                    DataFieldDTO dto = dataFieldRepository.selectDTOByPrimaryKey(aimDTO.getStandardId());
                    if (Objects.isNull(dto)) {
                        throw new CommonException(ErrorCode.DATA_FIELD_STANDARD_NOT_EXIST);
                    }

                    //执行验证逻辑
                    //定义一个验证标识，是否有必要继续验证
                    aimStatisticsDTO.setValidFlag(true);
                    statisticValidatorList.forEach(statisticValidator -> {
                        if (aimStatisticsDTO.isValidFlag()) {
                            statisticValidator.valid(dto, aimDTO, aimStatisticsDTO);
                        }
                    });


                    // 统计总行合规比例
                    String compliantRatePercent = getPercent(aimStatisticsDTO.getCompliantRow(), aimStatisticsDTO.getRowNum());
                    aimStatisticsDTO.setCompliantRate(compliantRatePercent);
                    // 统计非空行合规比列
                    String acompliantPercent = getPercent(aimStatisticsDTO.getNonNullRow(), aimStatisticsDTO.getRowNum());
                    aimStatisticsDTO.setAcompliantRate(acompliantPercent);
                    aimStatisticsDTOS.add(aimStatisticsDTO);
                } catch (Exception e) {
                    log.info("统计失败");
                    e.printStackTrace();
                } finally {
                    // 闭锁-1
                    countDownLatch.countDown();
                }
            });
        }
        try {
            //等待结果
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 落标总数统计
        aimStatisticsRepository.batchInsertDTOSelective(aimStatisticsDTOS);
        return dataFieldDTO;
    }


    /**
     * 计算百分比
     *
     * @param num1
     * @param num2
     * @return
     */
    private static String getPercent(Long num1, Long num2) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00%");
        String format = decimalFormat.format((float) num1 / num2);
        return format;

    }

    /**
     * 指定字段标准修改状态，供审批中，审批结束任务状态变更
     *
     * @param tenantId
     * @param fieldId
     * @param status
     */
    private void workflowing(Long tenantId, Long fieldId, String status) {
        DataFieldDTO dataFieldDTO = dataFieldRepository.selectDTOByPrimaryKey(
                DataFieldDTO.builder().fieldId(fieldId).tenantId(tenantId).build()
        );
        if (dataFieldDTO != null) {
            dataFieldDTO.setStandardStatus(status);
            dataFieldRepository.updateDTOOptional(dataFieldDTO, DataStandard.FIELD_STANDARD_STATUS);
            if (ONLINE.equals(status)) {
                doVersion(dataFieldDTO);
            }
        }
    }


}
