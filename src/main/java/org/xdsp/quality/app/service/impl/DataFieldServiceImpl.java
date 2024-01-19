package org.xdsp.quality.app.service.impl;


import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.hzero.boot.driver.app.service.DriverSessionService;
import org.hzero.boot.driver.infra.util.PageUtil;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.boot.platform.plugin.hr.EmployeeHelper;
import org.hzero.boot.platform.plugin.hr.entity.Employee;
import org.hzero.boot.workflow.WorkflowClient;
import org.hzero.boot.workflow.constant.WorkflowConstant;
import org.hzero.boot.workflow.dto.ProcessInstanceDTO;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.driver.core.infra.meta.Table;
import org.hzero.starter.driver.core.infra.util.PageParseUtil;
import org.hzero.starter.driver.core.session.DriverSession;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xdsp.core.CommonGroupClient;
import org.xdsp.core.domain.entity.CommonGroup;
import org.xdsp.core.domain.repository.CommonGroupRepository;
import org.xdsp.core.profile.XdspProfileClient;
import org.xdsp.quality.api.dto.*;
import org.xdsp.quality.app.service.DataFieldService;
import org.xdsp.quality.app.service.DataStandardService;
import org.xdsp.quality.app.service.StandardApprovalService;
import org.xdsp.quality.domain.entity.*;
import org.xdsp.quality.domain.repository.*;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.constant.PlanConstant;
import org.xdsp.quality.infra.constant.WorkFlowConstant;
import org.xdsp.quality.infra.converter.AimStatisticsConverter;
import org.xdsp.quality.infra.export.ExportUtils;
import org.xdsp.quality.infra.export.FieldStandardExporter;
import org.xdsp.quality.infra.export.dto.FieldStandardExportDTO;
import org.xdsp.quality.infra.mapper.DataFieldMapper;
import org.xdsp.quality.infra.mapper.DataStandardMapper;
import org.xdsp.quality.infra.mapper.StandardApprovalMapper;
import org.xdsp.quality.infra.statistic.validator.StatisticValidator;
import org.xdsp.quality.infra.util.CustomThreadPool;
import org.xdsp.quality.infra.util.DataTranslateUtil;
import org.xdsp.quality.infra.util.StandardHandler;
import org.xdsp.quality.workflow.adapter.DataFieldOfflineWorkflowAdapter;
import org.xdsp.quality.workflow.adapter.DataFieldOnlineWorkflowAdapter;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static org.xdsp.quality.infra.constant.PlanConstant.CheckType.STANDARD;
import static org.xdsp.quality.infra.constant.StandardConstant.StandardType.FIELD;
import static org.xdsp.quality.infra.constant.StandardConstant.Status.*;

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

    private final DataFieldVersionRepository dataFieldVersionRepository;

    private final DataFieldMapper dataFieldMapper;

    private final DataStandardService dataStandardService;

    private final StandardAimRepository standardAimRepository;

    private final ExtraVersionRepository extraVersionRepository;

    private final DataStandardMapper dataStandardMapper;

    private final ReferenceDataRepository referenceDataRepository;

    private final DataFieldOnlineWorkflowAdapter dataFieldOnlineWorkflowAdapter;

    private final DataFieldOfflineWorkflowAdapter dataFieldOfflineWorkflowAdapter;

    @Value("${xdsp.workflow.enabled:false}")
    private boolean enableWorkflow;

    @Autowired
    private WorkflowClient workflowClient;

    private final DriverSessionService driverSessionService;


    private final AimStatisticsRepository aimStatisticsRepository;

    private final StandardTeamRepository standardTeamRepository;

    private final StandardRelationRepository standardRelationRepository;

    private final StandardApprovalService standardApprovalService;

    private final StandardApprovalRepository standardApprovalRepository;

    private final StandardApprovalMapper standardApprovalMapper;

    private final AimStatisticsConverter aimStatisticsConverter;
    private final StandardGroupRepository standardGroupRepository;

    @Autowired
    private List<StandardHandler> handlers;

    private final List<StatisticValidator> statisticValidatorList;

    //统计行数
    private final static String COUNT_SQL = "SELECT COUNT(1) as COUNT FROM %s";

    //统计非空行数
    private final static String NOT_NULL_COUNT = "SELECT COUNT(*)  FROM %s where %s is not null";

    @Autowired
    private XdspProfileClient profileClient;

    private final DataTranslateUtil dataTranslateUtil;

    public DataFieldServiceImpl(DataFieldRepository dataFieldRepository, StandardExtraRepository standardExtraRepository,
                                DataFieldVersionRepository dataFieldVersionRepository, DataFieldMapper dataFieldMapper,
                                DataStandardService dataStandardService, ExtraVersionRepository extraVersionRepository,
                                DataStandardMapper dataStandardMapper, StandardAimRepository standardAimRepository,
                                ReferenceDataRepository referenceDataRepository, DataFieldOnlineWorkflowAdapter dataFieldOnlineWorkflowAdapter,
                                DataFieldOfflineWorkflowAdapter dataFieldOfflineWorkflowAdapter,
                                StandardTeamRepository standardTeamRepository, StandardRelationRepository standardRelationRepository,
                                AimStatisticsRepository aimStatisticsRepository, StandardApprovalRepository standardApprovalRepository,
                                StandardApprovalMapper standardApprovalMapper, List<StatisticValidator> statisticValidatorList,
                                DriverSessionService driverSessionService, StandardApprovalService standardApprovalService, AimStatisticsConverter aimStatisticsConverter, StandardGroupRepository standardGroupRepository, DataTranslateUtil dataTranslateUtil) {
        this.dataFieldRepository = dataFieldRepository;
        this.standardExtraRepository = standardExtraRepository;
        this.dataFieldVersionRepository = dataFieldVersionRepository;
        this.dataFieldMapper = dataFieldMapper;
        this.dataStandardService = dataStandardService;
        this.standardAimRepository = standardAimRepository;
        this.extraVersionRepository = extraVersionRepository;
        this.dataStandardMapper = dataStandardMapper;
        this.referenceDataRepository = referenceDataRepository;
        this.dataFieldOnlineWorkflowAdapter = dataFieldOnlineWorkflowAdapter;
        this.dataFieldOfflineWorkflowAdapter = dataFieldOfflineWorkflowAdapter;

        this.standardTeamRepository = standardTeamRepository;
        this.standardRelationRepository = standardRelationRepository;
        this.standardApprovalRepository = standardApprovalRepository;
        this.standardApprovalMapper = standardApprovalMapper;
        this.driverSessionService = driverSessionService;
        this.aimStatisticsRepository = aimStatisticsRepository;
        this.statisticValidatorList = statisticValidatorList;
        this.standardApprovalService = standardApprovalService;
        this.aimStatisticsConverter = aimStatisticsConverter;
        this.standardGroupRepository = standardGroupRepository;
        this.dataTranslateUtil = dataTranslateUtil;
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
                            .tenantId(dataFieldDTO.getTenantId())
                            .projectId(dataFieldDTO.getProjectId())
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
        if (DataSecurityHelper.isTenantOpen()) {
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
            if (StringUtils.isNotEmpty(dataFieldDTO.getChargeName())) {
                dataFieldDTO.setChargeName(DataSecurityHelper.decrypt(dataFieldDTO.getChargeName()));
            }

        }
        if (PlanConstant.StandardValueType.REFERENCE_DATA.equals(dataFieldDTO.getValueType()) && StringUtils.isNotBlank(dataFieldDTO.getValueRange())) {
            long referenceDataId = Long.parseLong(dataFieldDTO.getValueRange());
            ReferenceDataDTO referenceDataDTO = referenceDataRepository.selectDTOByPrimaryKey(referenceDataId);
            if (Objects.nonNull(referenceDataDTO)) {
                dataFieldDTO.setReferenceDataCode(referenceDataDTO.getDataCode());
                dataFieldDTO.setReferenceDataName(referenceDataDTO.getDataName());
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

        // 翻译值域范围: 翻译失败，返回原valueRange
        String valueRange = dataTranslateUtil.translateValueRange(dataFieldDTO.getValueType(), dataFieldDTO.getValueRange(), tenantId);
        dataFieldDTO.setValueRange(valueRange);

        return dataFieldDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(DataFieldDTO dataFieldDTO) {
        if (ONLINE.equals(dataFieldDTO.getStandardStatus())
                || OFFLINE_APPROVING.equals(dataFieldDTO.getStandardStatus())
                || ONLINE_APPROVING.equals(dataFieldDTO.getStandardStatus())) {
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

        // 删除审批记录
        standardApprovalService.delete(StandardApprovalDTO
                .builder()
                .standardId(dataFieldDTO.getFieldId())
                .standardType("FIELD")
                .build());
    }

    @Override
    public List<DataFieldDTO> findDataFieldDtoList(DataFieldDTO dataFieldDTO) {
        return dataFieldMapper.list(dataFieldDTO);
    }

    @Override
    public Page<DataFieldDTO> list(PageRequest pageRequest, DataFieldDTO dataFieldDTO) {
        //分组查询时同时查询当前分组和当前分组子分组的数据标准
        Long groupId = dataFieldDTO.getGroupId();
        if (ObjectUtils.isNotEmpty(groupId)) {
//            List<StandardGroupDTO> standardGroupDTOList = new ArrayList<>();
//            //查询子分组
//            findChildGroups(groupId, standardGroupDTOList);
//            //添加当前分组
//            standardGroupDTOList.add(StandardGroupDTO.builder().groupId(groupId).build());
//            Long[] groupIds = standardGroupDTOList.stream().map(StandardGroupDTO::getGroupId).toArray(Long[]::new);
//            dataFieldDTO.setGroupArrays(groupIds);
            CommonGroupRepository commonGroupRepository = ApplicationContextHelper.getContext().getBean(CommonGroupRepository.class);
            CommonGroup commonGroup = commonGroupRepository.selectByPrimaryKey(groupId);
            CommonGroupClient commonGroupClient = ApplicationContextHelper.getContext().getBean(CommonGroupClient.class);
            List<CommonGroup> subGroup = commonGroupClient.getSubGroup(commonGroup);
            subGroup.add(commonGroup);
            dataFieldDTO.setGroupArrays(subGroup.stream().map(CommonGroup::getGroupId).toArray(Long[]::new));
        }
        List<DataFieldDTO> dataFieldDTOList = dataFieldMapper.list(dataFieldDTO);
        ExportUtils.decryptFieldStandard(dataFieldDTOList);
        return PageParseUtil.springPage2C7nPage(PageUtil.doPage(dataFieldDTOList, org.springframework.data.domain.PageRequest.of(pageRequest.getPage(), pageRequest.getSize())));
    }

    private void findChildGroups(Long groupId, List<StandardGroupDTO> standardGroups) {
        List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_PARENT_GROUP_ID, groupId))
                .build());
        if (CollectionUtils.isNotEmpty(standardGroupDTOList)) {
            standardGroups.addAll(standardGroupDTOList);
            standardGroupDTOList.forEach(standardGroupDTO -> findChildGroups(standardGroupDTO.getGroupId(), standardGroups));
        }
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
        //一个工作流的总开关
        String onlineOpen = profileClient.getProfileValue(dataFieldDTO.getTenantId(), dataFieldDTO.getProjectId(), WorkFlowConstant.OpenConfig.FIELD_STANDARD_ONLINE);
        String offlineOpen = profileClient.getProfileValue(dataFieldDTO.getTenantId(), dataFieldDTO.getProjectId(), WorkFlowConstant.OpenConfig.FIELD_STANDARD_OFFLINE);

        DataFieldDTO dto = dataFieldRepository.selectDTOByPrimaryKey(dataFieldDTO.getFieldId());
        if (Objects.isNull(dto)) {
            throw new CommonException(ErrorCode.DATA_FIELD_STANDARD_NOT_EXIST);
        }
        if (ONLINE_APPROVING.equals(dto.getStandardStatus())
                || OFFLINE_APPROVING.equals(dto.getStandardStatus())) {
            //前端数据传递有问题，状态检验
            throw new CommonException("状态正在审批中，无法操作");
        }
        if (ONLINE.equals(dataFieldDTO.getStandardStatus())) {
            if ((onlineOpen == null || Boolean.parseBoolean(onlineOpen))) {
                //指定字段标准修改状态
                dataFieldDTO.setStandardStatus(ONLINE_APPROVING);
                dataFieldOnlineWorkflowAdapter.startWorkflow(dataFieldDTO);
            } else {
                dataFieldDTO.setObjectVersionNumber(dto.getObjectVersionNumber());
                dataFieldDTO.setReleaseBy(DetailsHelper.getUserDetails().getUserId());
                dataFieldDTO.setReleaseDate(new Date());
                //存版本表
                doVersion(dataFieldDTO);
                dataFieldRepository.updateDTOOptional(dataFieldDTO, DataField.FIELD_STANDARD_STATUS, DataField.FIELD_RELEASE_BY, DataField.FIELD_RELEASE_DATE);
                //存版本表
                doVersion(dataFieldDTO);
                return;
            }
        }

        if (OFFLINE.equals(dataFieldDTO.getStandardStatus())) {
            if ((offlineOpen == null || Boolean.parseBoolean(offlineOpen))) {
                //指定字段标准修改状态
                dataFieldDTO.setStandardStatus(OFFLINE_APPROVING);
                dataFieldOfflineWorkflowAdapter.startWorkflow(dataFieldDTO);
            } else {
                dataFieldDTO.setObjectVersionNumber(dto.getObjectVersionNumber());
            }
        }
        dataFieldRepository.updateDTOOptional(dataFieldDTO, DataField.FIELD_STANDARD_STATUS);
    }

    @Override
    @ProcessLovValue(targetField = "dataFieldDTOList")
    public List<FieldStandardExportDTO> export(DataFieldDTO dto, ExportParam exportParam) {
        return ApplicationContextHelper.getContext().getBean(FieldStandardExporter.class).export(dto);
    }

    @Override
    public void doVersion(DataFieldDTO dataFieldDTO) {
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
    public void onlineWorkflowCallback(Long fieldId, String nodeApproveResult) {
        nodeApproveResult = (String) dataFieldOnlineWorkflowAdapter.callBack(fieldId, nodeApproveResult);
        if (WorkflowConstant.ApproveAction.APPROVED.equals(nodeApproveResult)) {
            workflowing(DetailsHelper.getUserDetails().getTenantId(), fieldId, ONLINE);
        } else {
            workflowing(DetailsHelper.getUserDetails().getTenantId(), fieldId, OFFLINE);
        }
    }

    @Override
    public void offlineWorkflowCallback(Long fieldId, String nodeApproveResult) {
        nodeApproveResult = (String) dataFieldOfflineWorkflowAdapter.callBack(fieldId, nodeApproveResult);
        if (WorkflowConstant.ApproveAction.APPROVED.equals(nodeApproveResult)) {
            workflowing(DetailsHelper.getUserDetails().getTenantId(), fieldId, OFFLINE);
        } else {
            workflowing(DetailsHelper.getUserDetails().getTenantId(), fieldId, ONLINE);
        }
    }

    @Override
    public List<AssigneeUserDTO> findCharger(Long tenantId, Long fieldId) {
        DataFieldDTO dataFieldDTO = dataFieldRepository.selectDTOByPrimaryKey(
                DataFieldDTO.builder().fieldId(fieldId).tenantId(tenantId).build()
        );
        if (dataFieldDTO != null) {
            AssigneeUserDTO assigneeUserDTO = dataStandardMapper.selectAssigneeUser(dataFieldDTO.getChargeId());
            //查询员工责任人并解密
            if (DataSecurityHelper.isTenantOpen()) {
                if (StringUtils.isNotEmpty(assigneeUserDTO.getEmployeeName())) {
                    assigneeUserDTO.setEmployeeName(DataSecurityHelper.decrypt(assigneeUserDTO.getEmployeeName()));
                }
                if (StringUtils.isNotEmpty(assigneeUserDTO.getEmployeeNum())) {
                    assigneeUserDTO.setEmployeeNum(DataSecurityHelper.decrypt(assigneeUserDTO.getEmployeeNum()));
                }
            }
            return Collections.singletonList(assigneeUserDTO);
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
                            .tenantId(dataFieldDTO.getTenantId())
                            .projectId(dataFieldDTO.getProjectId())
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
                .ruleName(dataFieldDTO.getFieldComment() + "自动生成规则")
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
    @Transactional(rollbackFor = Exception.class)
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
        //获取所有的落标记录
        List<Long> aimIds = standardAimDTOS.stream().map(StandardAimDTO::getAimId).collect(Collectors.toList());

        //新的落标统计记录
        List<AimStatisticsDTO> aimStatisticsDTOS = new CopyOnWriteArrayList<>();

        CompletionService executor = new ExecutorCompletionService<>(CustomThreadPool.getExecutor());
        List<Future> futures = new ArrayList<>();
        for (StandardAimDTO aimDTO : standardAimDTOS) {
            Future future = executor.submit(() -> {
                try {
                    AimStatisticsDTO aimStatisticsDTO = new AimStatisticsDTO();
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
                        String notNullCount = notNullResult.get(0).values().toArray()[0].toString();
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
                    BigDecimal compliantRatePercent = getPercent(aimStatisticsDTO.getCompliantRow(), aimStatisticsDTO.getRowNum());
                    aimStatisticsDTO.setCompliantRate(compliantRatePercent);
                    // 统计非空行合规比列
                    BigDecimal acompliantPercent = getPercent(aimStatisticsDTO.getCompliantRow(), aimStatisticsDTO.getNonNullRow());
                    aimStatisticsDTO.setAcompliantRate(acompliantPercent);
                    aimStatisticsDTOS.add(aimStatisticsDTO);
                } catch (Exception e) {
                    throw new CommonException("落标统计失败", e);
                }
                return null;
            });
            futures.add(future);
        }

        //如果存在失败的情况，则直接取消落标统计
        for (Future future : futures) {
            try {
                future = executor.take();
                future.get();
            } catch (Exception e) {
                //如果执行失败的话，取消其他任务
                futures.forEach(f1 -> {
                    if (!f1.isDone()) {
                        log.info("取消正常运行的任务，避免资源浪费");
                        f1.cancel(true);
                    }
                });
                throw new CommonException("落标统计失败", e);
            }
        }

        // 落标总数统计
        if (CollectionUtils.isNotEmpty(aimStatisticsDTOS)) {
            List<AimStatistics> oldAimStatistics = aimStatisticsRepository.selectByCondition(Condition.builder(AimStatistics.class)
                    .andWhere(Sqls.custom().andIn(AimStatistics.FIELD_AIM_ID, aimIds))
                    .build());

            aimStatisticsRepository.batchDeleteByPrimaryKey(oldAimStatistics);
            aimStatisticsRepository.batchInsertDTOSelective(aimStatisticsDTOS);
        }
        return dataFieldDTO;
    }


    /**
     * 计算百分比
     *
     * @param num1
     * @param num2
     * @return
     */
    private static BigDecimal getPercent(Long num1, Long num2) {
        if (num2 == 0) {
            return BigDecimal.valueOf(0);
        } else {
            return BigDecimal.valueOf(num1).divide(BigDecimal.valueOf(num2), 2, BigDecimal.ROUND_HALF_UP);
        }
    }

    /**
     * 指定字段标准修改状态，供审批中，审批结束任务状态变更
     *
     * @param tenantId
     * @param fieldId
     * @param status
     */
    @Override
    public void workflowing(Long tenantId, Long fieldId, String status) {
        DataFieldDTO dataFieldDTO = dataFieldRepository.selectDTOByPrimaryKey(
                DataFieldDTO.builder().fieldId(fieldId).tenantId(tenantId).build()
        );
        if (dataFieldDTO != null) {
            // 判断流程类型，当字段标准状态不为ONLINE且调用workflowing status传值ONLINE时则为发布上线
            if (!ONLINE.equals(dataFieldDTO.getStandardStatus()) && ONLINE.equals(status)) {
                List<StandardApprovalDTO> standardApprovalDTOS = standardApprovalRepository.selectDTOByCondition(Condition.builder(StandardApproval.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(StandardApproval.FIELD_TENANT_ID, tenantId)
                                .andEqualTo(StandardApproval.FIELD_STANDARD_ID, fieldId)
                                .andEqualTo(StandardApproval.FIELD_STANDARD_TYPE, FIELD)
                                .andEqualTo(StandardApproval.FIELD_APPLY_TYPE, ONLINE))
                        .orderByDesc(StandardApproval.FIELD_APPROVAL_ID)
                        .build());
                if (CollectionUtils.isNotEmpty(standardApprovalDTOS)) {
                    StandardApprovalDTO standardApprovalDTO = standardApprovalDTOS.get(0);
                    dataFieldDTO.setReleaseBy(standardApprovalDTO.getCreatedBy());
                    dataFieldDTO.setReleaseDate(new Date());
                }
                dataFieldDTO.setStandardStatus(status);
                dataFieldRepository.updateDTOOptional(dataFieldDTO, DataField.FIELD_STANDARD_STATUS, DataField.FIELD_RELEASE_BY, DataField.FIELD_RELEASE_DATE);
            } else {
                dataFieldDTO.setStandardStatus(status);
                dataFieldRepository.updateDTOOptional(dataFieldDTO, DataField.FIELD_STANDARD_STATUS);
            }
            if (ONLINE.equals(status)) {
                doVersion(dataFieldDTO);
            }
        }
    }

    @Override
    public List<DataFieldDTO> listAll(DataFieldDTO dataFieldDTO) {
        return dataFieldMapper.list(dataFieldDTO);
    }

    @Override
    public void fieldAimStandard(AssetFieldDTO assetFieldDTO, Long projectId) {
        // 根据字段删除 字段标准落标
        String fieldName = String.format("%s(%s)", assetFieldDTO.getFieldName(), assetFieldDTO.getFieldType().toUpperCase());
        List<StandardAim> standardAimList = standardAimRepository.select(StandardAim
                .builder()
                .standardType(FIELD)
                .datasourceCode(assetFieldDTO.getDatasourceCode())
                .schemaName(assetFieldDTO.getDatasourceSchema())
                .tableName(assetFieldDTO.getTableName())
                .fieldName(fieldName)
                .tenantId(assetFieldDTO.getTenantId())
                .projectId(projectId)
                .build());
        standardAimRepository.batchDeleteByPrimaryKey(standardAimList);
        //创建新的字段落标
        List<Long> standardIdList = assetFieldDTO.getStandardIdList();
        if (CollectionUtils.isNotEmpty(standardIdList)) {
            for (Long standardId : standardIdList) {
                DataFieldDTO dataFieldDTO = dataFieldRepository.selectDTOByPrimaryKey(standardId);
                if (Objects.isNull(dataFieldDTO)) {
                    throw new CommonException(ErrorCode.DATA_FIELD_STANDARD_NOT_EXIST);
                }
                StandardAimDTO standardAimDTO = StandardAimDTO
                        .builder()
                        .standardId(standardId)
                        .standardType(FIELD)
                        .fieldName(fieldName)
                        .fieldDesc(assetFieldDTO.getFieldDesc())
                        .datasourceId(assetFieldDTO.getDatasourceId())
                        .datasourceCode(assetFieldDTO.getDatasourceCode())
                        .datasourceType(assetFieldDTO.getDatasourceType())
                        .schemaName(assetFieldDTO.getDatasourceSchema())
                        .tableName(assetFieldDTO.getTableName())
                        .tenantId(assetFieldDTO.getTenantId())
                        .projectId(projectId)
                        .build();
                DriverSession driverSession = driverSessionService.getDriverSession(assetFieldDTO.getTenantId(), assetFieldDTO.getDatasourceCode());
                List<Table> tables = driverSession.tablesNameAndDesc(assetFieldDTO.getDatasourceSchema());
                Optional<Table> first = tables.stream().filter(table -> assetFieldDTO.getTableName().equals(table.getTableName())).findFirst();
                first.ifPresent(table -> standardAimDTO.setTableDesc(table.getRemarks()));
                standardAimRepository.insertDTOSelective(standardAimDTO);
            }
        }
    }


    @Override
    public List<DataFieldDTO> standardByField(AssetFieldDTO assetFieldDTO, Long projectId) {
        List<DataFieldDTO> dataFieldDTOList = new ArrayList<>();
        String fieldName = String.format("%s(%s)", assetFieldDTO.getFieldName(), assetFieldDTO.getFieldType().toUpperCase());
        List<StandardAimDTO> standardAimDTOList = standardAimRepository.selectDTOByCondition(Condition.builder(StandardAim.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(StandardAim.FIELD_STANDARD_TYPE, FIELD)
                        .andEqualTo(StandardAim.FIELD_DATASOURCE_CODE, assetFieldDTO.getDatasourceCode())
                        .andEqualTo(StandardAim.FIELD_SCHEMA_NAME, assetFieldDTO.getDatasourceSchema())
                        .andEqualTo(StandardAim.FIELD_TABLE_NAME, assetFieldDTO.getTableName())
                        .andEqualTo(StandardAim.FIELD_FIELD_NAME, fieldName)
                        .andEqualTo(StandardAim.FIELD_TENANT_ID, assetFieldDTO.getTenantId())
                        .andEqualTo(StandardAim.FIELD_PROJECT_ID, projectId))
                .build());
        if (CollectionUtils.isNotEmpty(standardAimDTOList)) {
            for (StandardAimDTO standardAimDTO : standardAimDTOList) {
                DataFieldDTO dataFieldDTO = dataFieldRepository.selectDTOByPrimaryKey(standardAimDTO.getStandardId());
                dataFieldDTOList.add(dataFieldDTO);
            }
        }
        return dataFieldDTOList;
    }

    @Override
    public StandardApprovalDTO fieldApplyInfo(Long tenantId, Long approvalId) {
        StandardApprovalDTO standardApprovalDTO = standardApprovalRepository.selectDTOByPrimaryKey(approvalId);
        if (!(Objects.nonNull(standardApprovalDTO) && Objects.nonNull(standardApprovalDTO.getApprovalId()))) {
            throw new CommonException(ErrorCode.NO_APPROVAL_INSTANCE);
        }
        ProcessInstanceDTO.ProcessInstanceViewDTO instanceView = workflowClient.getInstanceDetailByInstanceId(tenantId, standardApprovalDTO.getInstanceId());
        List<ProcessInstanceDTO.ProcessInstanceCurrentNodeDTO> nodeDTOList = instanceView.getNodeDTOList();
        StandardApprovalDTO approvalDTO = StandardApprovalDTO
                .builder()
                .flowName(instanceView.getFlowName())
                .businessKey(instanceView.getBusinessKey())
                .applyDate(instanceView.getStartDate())
                .build();
        UserDTO userInfo = standardApprovalMapper.getUserInfo(standardApprovalDTO.getApplicantId());
        approvalDTO.setEmployeeTel(userInfo.getPhone());
        approvalDTO.setEmployeeEmail(userInfo.getEmail());
        approvalDTO.setEmployeeName(userInfo.getRealName());
        Employee employee = EmployeeHelper.getEmployee(standardApprovalDTO.getApplicantId(), standardApprovalDTO.getTenantId());
        List<String> employeeUnitList = standardApprovalMapper.getEmployeeUnit(employee);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(employeeUnitList)) {
            String unitName = employeeUnitList.get(0);
            if (DataSecurityHelper.isTenantOpen()) {
                unitName = DataSecurityHelper.decrypt(unitName);
            }
            approvalDTO.setApplyUnitName(unitName);
        }
        if (CollectionUtils.isNotEmpty(nodeDTOList)) {
            approvalDTO.setCurrentNode(nodeDTOList.get(0).getCurrentNode());
        }
        long lastVersion = 1L;
        List<DataFieldVersionDTO> dataFieldVersionDTOList = dataFieldVersionRepository.selectDTOByCondition(Condition.builder(DataFieldVersion.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataField.FIELD_FIELD_ID, standardApprovalDTO.getStandardId()))
                .orderByDesc(DataFieldVersion.FIELD_VERSION_NUMBER).build());
        //不为空则取最新版本
        if (CollectionUtils.isNotEmpty(dataFieldVersionDTOList)) {
            lastVersion = dataFieldVersionDTOList.get(0).getVersionNumber() + 1;
        }
        approvalDTO.setSubmitVersion(String.format("v%s.0", lastVersion));
        return approvalDTO;
    }

    @Override
    public DataFieldDTO fieldInfo(Long tenantId, Long approvalId) {
        StandardApprovalDTO standardApprovalDTO = standardApprovalRepository.selectDTOByPrimaryKey(approvalId);
        if (Objects.isNull(standardApprovalDTO)) {
            throw new CommonException(ErrorCode.NO_APPROVAL_INSTANCE);
        }
        return this.detail(standardApprovalDTO.getTenantId(), standardApprovalDTO.getStandardId());
    }
}
