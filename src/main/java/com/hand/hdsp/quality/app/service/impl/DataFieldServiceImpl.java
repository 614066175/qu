package com.hand.hdsp.quality.app.service.impl;


import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.DataFieldService;
import com.hand.hdsp.quality.app.service.DataStandardService;
import com.hand.hdsp.quality.app.service.StandardApprovalService;
import com.hand.hdsp.quality.domain.entity.*;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.converter.AimStatisticsConverter;
import com.hand.hdsp.quality.infra.mapper.DataFieldMapper;
import com.hand.hdsp.quality.infra.mapper.DataStandardMapper;
import com.hand.hdsp.quality.infra.mapper.StandardApprovalMapper;
import com.hand.hdsp.quality.infra.statistic.validator.StatisticValidator;
import com.hand.hdsp.quality.infra.util.CustomThreadPool;
import com.hand.hdsp.quality.infra.util.StandardHandler;
import com.hand.hdsp.quality.workflow.adapter.DataFieldOfflineWorkflowAdapter;
import com.hand.hdsp.quality.workflow.adapter.DataFieldOnlineWorkflowAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static com.hand.hdsp.quality.infra.constant.PlanConstant.CheckType.STANDARD;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.StandardType.FIELD;
import static com.hand.hdsp.quality.infra.constant.StandardConstant.Status.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

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

    private final DataFieldOnlineWorkflowAdapter dataFieldOnlineWorkflowAdapter;

    private final DataFieldOfflineWorkflowAdapter dataFieldOfflineWorkflowAdapter;

    @Value("${hdsp.workflow.enabled:false}")
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

    public DataFieldServiceImpl(DataFieldRepository dataFieldRepository, StandardExtraRepository standardExtraRepository,
                                DataFieldVersionRepository dataFieldVersionRepository, DataFieldMapper dataFieldMapper,
                                DataStandardService dataStandardService, ExtraVersionRepository extraVersionRepository,
                                DataStandardMapper dataStandardMapper, StandardAimRepository standardAimRepository,
                                DataFieldOnlineWorkflowAdapter dataFieldOnlineWorkflowAdapter,
                                DataFieldOfflineWorkflowAdapter dataFieldOfflineWorkflowAdapter,
                                StandardTeamRepository standardTeamRepository, StandardRelationRepository standardRelationRepository,
                                AimStatisticsRepository aimStatisticsRepository, StandardApprovalRepository standardApprovalRepository,
                                StandardApprovalMapper standardApprovalMapper, List<StatisticValidator> statisticValidatorList,
                                DriverSessionService driverSessionService, StandardApprovalService standardApprovalService, AimStatisticsConverter aimStatisticsConverter, StandardGroupRepository standardGroupRepository) {
        this.dataFieldRepository = dataFieldRepository;
        this.standardExtraRepository = standardExtraRepository;
        this.dataFieldVersionRepository = dataFieldVersionRepository;
        this.dataFieldMapper = dataFieldMapper;
        this.dataStandardService = dataStandardService;
        this.standardAimRepository = standardAimRepository;
        this.extraVersionRepository = extraVersionRepository;
        this.dataStandardMapper = dataStandardMapper;
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
            List<StandardGroupDTO> standardGroupDTOList = new ArrayList<>();
            //查询子分组
            findChildGroups(groupId, standardGroupDTOList);
            //添加当前分组
            standardGroupDTOList.add(StandardGroupDTO.builder().groupId(groupId).build());
            Long[] groupIds = standardGroupDTOList.stream().map(StandardGroupDTO::getGroupId).toArray(Long[]::new);
            dataFieldDTO.setGroupArrays(groupIds);
        }
        List<DataFieldDTO> dataFieldDTOList = dataFieldMapper.list(dataFieldDTO);
        dataFieldDTOList.forEach(dataFieldDto -> {
            if (DataSecurityHelper.isTenantOpen()) {
                //解密邮箱，电话
                if (Strings.isNotEmpty(dataFieldDto.getChargeTel())) {
                    dataFieldDto.setChargeTel(DataSecurityHelper.decrypt(dataFieldDto.getChargeTel()));
                }
                if (Strings.isNotEmpty(dataFieldDto.getChargeEmail())) {
                    dataFieldDto.setChargeEmail(DataSecurityHelper.decrypt(dataFieldDto.getChargeEmail()));
                }
                if (Strings.isNotEmpty(dataFieldDto.getChargeDeptName())) {
                    dataFieldDto.setChargeDeptName(DataSecurityHelper.decrypt(dataFieldDto.getChargeDeptName()));
                }
                if (StringUtils.isNotEmpty(dataFieldDto.getChargeName())) {
                    dataFieldDto.setChargeName(DataSecurityHelper.decrypt(dataFieldDto.getChargeName()));
                }
            }
        });
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
        if (enableWorkflow) {
            //开启工作流
            //根据上下线状态开启不同的工作流实例
            if (ONLINE.equals(dataFieldDTO.getStandardStatus())) {
                //指定字段标准修改状态
                dataFieldDTO.setStandardStatus(ONLINE_APPROVING);
                dataFieldOnlineWorkflowAdapter.startWorkflow(dataFieldDTO);
            }
            if (OFFLINE.equals(dataFieldDTO.getStandardStatus())) {
                //指定字段标准修改状态
                dataFieldDTO.setStandardStatus(OFFLINE_APPROVING);
                dataFieldOfflineWorkflowAdapter.startWorkflow(dataFieldDTO);
            }
        } else {
            DataFieldDTO dto = dataFieldRepository.selectDTOByPrimaryKey(dataFieldDTO.getFieldId());
            if (Objects.isNull(dto)) {
                throw new CommonException(ErrorCode.DATA_FIELD_STANDARD_NOT_EXIST);
            }
            dataFieldDTO.setObjectVersionNumber(dto.getObjectVersionNumber());
            if (ONLINE.equals(dataFieldDTO.getStandardStatus())) {
                //存版本表
                doVersion(dataFieldDTO);
            }
        }
        dataFieldRepository.updateDTOOptional(dataFieldDTO, DataStandard.FIELD_STANDARD_STATUS);
    }

    @Override
    @ProcessLovValue(targetField = "dataFieldDTOList")
    public List<DataFieldGroupDTO> export(DataFieldDTO dto, ExportParam exportParam) {
        List<DataFieldGroupDTO> dataFieldGroupDTOList = new ArrayList<>();
        DataFieldGroupDTO dataFieldGroupDTO = new DataFieldGroupDTO();
        Long projectId = ProjectHelper.getProjectId();
        int level = 1;
        if (ObjectUtils.isNotEmpty(dto.getGroupId())) {
            //分组条件导出
            StandardGroupDTO groupDTO = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                    .andEqualTo(StandardGroup.FIELD_TENANT_ID, dto.getTenantId())
                    .andEqualTo(StandardGroup.FIELD_PROJECT_ID, projectId)
                    .andEqualTo(StandardGroup.FIELD_GROUP_ID, dto.getGroupId(), true)
            ).build()).get(0);
            //获取设置当前分组的父分组编码
            if (ObjectUtils.isNotEmpty(groupDTO.getParentGroupId())) {
                StandardGroupDTO parentGroupDTO = standardGroupRepository.selectDTOByPrimaryKey(groupDTO.getParentGroupId());
                groupDTO.setParentGroupCode(parentGroupDTO.getGroupCode());
            }
            BeanUtils.copyProperties(groupDTO, dataFieldGroupDTO);
            List<StandardGroupDTO> standardGroups = new ArrayList<>();
            //导出分组下条件筛选后的数据标准
            Long groupId = dataFieldGroupDTO.getGroupId();
            if (ObjectUtils.isNotEmpty(groupId)) {
                //添加当前分组
                standardGroups.add(StandardGroupDTO.builder().groupId(groupId).build());
                Long[] groupIds = standardGroups.stream().map(StandardGroupDTO::getGroupId).toArray(Long[]::new);
                dto.setGroupArrays(groupIds);
            }
            //当前目录和子目录的数据标准的集合，与查询保持一致
            List<DataFieldDTO> dataFields = dataFieldMapper.list(dto);
            dataFields.forEach(dataFieldDTO -> {
                //查询标准组
                List<StandardRelation> standardRelations = standardRelationRepository.select(StandardRelation.builder().fieldStandardId(dataFieldDTO.getFieldId()).build());
                List<Long> standardTeamIds = standardRelations.stream()
                        .map(StandardRelation::getStandardTeamId)
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(standardTeamIds)) {
                    List<StandardTeamDTO> standardTeamDTOS = standardTeamRepository.selectDTOByIds(standardTeamIds);
                    //标准组导出
                    dataFieldDTO.setStandardTeamCode(StringUtils.join(standardTeamDTOS.stream().map(StandardTeamDTO::getStandardTeamCode).toArray(), ";"));
                }
            });
            //导出解密责任人信息
            if(DataSecurityHelper.isTenantOpen() && CollectionUtils.isNotEmpty(dataFields)){
                dataFields.forEach(dataFieldDTO -> {
                    if(StringUtils.isNotEmpty(dataFieldDTO.getChargeName())){
                        dataFieldDTO.setChargeName(DataSecurityHelper.decrypt(dataFieldDTO.getChargeName()));
                    }
                    if(StringUtils.isNotEmpty(dataFieldDTO.getChargeDeptName())){
                        dataFieldDTO.setChargeDeptName(DataSecurityHelper.decrypt(dataFieldDTO.getChargeDeptName()));
                    }
                    if(StringUtils.isNotEmpty(dataFieldDTO.getChargeTel())){
                        dataFieldDTO.setChargeTel(DataSecurityHelper.decrypt(dataFieldDTO.getChargeTel()));
                    }
                    if(StringUtils.isNotEmpty(dataFieldDTO.getChargeEmail())){
                        dataFieldDTO.setChargeEmail(DataSecurityHelper.decrypt(dataFieldDTO.getChargeEmail()));
                    }
                });
            }
            dataFieldGroupDTO.setDataFieldDTOList(dataFields);
            dataFieldGroupDTO.setGroupLevel(level);
            dataFieldGroupDTOList.add(dataFieldGroupDTO);
            //添加查询父分组 并排序导出保证导入准确性
            List<DataFieldGroupDTO> dataFieldGroupDTOS = new ArrayList<>();
            if (ObjectUtils.isNotEmpty(groupDTO.getParentGroupId())) {
                findParentGroups(groupDTO.getParentGroupId(), dataFieldGroupDTOS, level);
            }
            dataFieldGroupDTOList.addAll(dataFieldGroupDTOS);
            return dataFieldGroupDTOList.stream().sorted(Comparator.comparing(DataFieldGroupDTO::getGroupLevel).reversed()).collect(Collectors.toList());
        } else {
            //全部分组条件导出
            //添加查询所有父分组 并排序导出保证导入准确性
            List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                            .andEqualTo(StandardGroup.FIELD_TENANT_ID, dto.getTenantId())
                            .andEqualTo(StandardGroup.FIELD_PROJECT_ID, dto.getProjectId())
                            .andEqualTo(StandardGroup.FIELD_STANDARD_TYPE, FIELD))
                    .build());
            standardGroupDTOList.forEach(standardGroupDTO -> {
                if (ObjectUtils.isEmpty(standardGroupDTO.getParentGroupId())) {
                    //从所有的根目录 向下查询
                    DataFieldGroupDTO dataFieldGroupDto = new DataFieldGroupDTO();
                    BeanUtils.copyProperties(standardGroupDTO, dataFieldGroupDto);
                    //根目录数据标准列表
                    List<DataFieldDTO> dataFieldDTOList = dataFieldMapper.list(DataFieldDTO.builder().groupArrays(new Long[]{dataFieldGroupDto.getGroupId()}).build());
                    dataFieldDTOList.forEach(dataFieldDTO -> {
                        //查询标准组
                        List<StandardRelation> standardRelations = standardRelationRepository.select(StandardRelation.builder().fieldStandardId(dataFieldDTO.getFieldId()).build());
                        List<Long> standardTeamIds = standardRelations.stream()
                                .map(StandardRelation::getStandardTeamId)
                                .collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(standardTeamIds)) {
                            List<StandardTeamDTO> standardTeamDTOS = standardTeamRepository.selectDTOByIds(standardTeamIds);
                            //标准组导出
                            dataFieldDTO.setStandardTeamCode(StringUtils.join(standardTeamDTOS.stream().map(StandardTeamDTO::getStandardTeamCode).toArray(), ";"));
                        }
                    });
                    if (DataSecurityHelper.isTenantOpen() && CollectionUtils.isNotEmpty(dataFieldDTOList)) {
                        dataFieldDTOList.forEach(dataFieldDTO -> {
                            if (StringUtils.isNotEmpty(dataFieldDTO.getChargeName())) {
                                dataFieldDTO.setChargeName(DataSecurityHelper.decrypt(dataFieldDTO.getChargeName()));
                            }
                            if (StringUtils.isNotEmpty(dataFieldDTO.getChargeTel())) {
                                dataFieldDTO.setChargeTel(DataSecurityHelper.decrypt(dataFieldDTO.getChargeTel()));
                            }
                            if (StringUtils.isNotEmpty(dataFieldDTO.getChargeEmail())) {
                                dataFieldDTO.setChargeEmail(DataSecurityHelper.decrypt(dataFieldDTO.getChargeEmail()));
                            }
                            if (StringUtils.isNotEmpty(dataFieldDTO.getChargeDeptName())) {
                                dataFieldDTO.setChargeDeptName(DataSecurityHelper.decrypt(dataFieldDTO.getChargeDeptName()));
                            }
                        });
                    }
                    dataFieldGroupDto.setDataFieldDTOList(dataFieldDTOList);
                    dataFieldGroupDto.setGroupLevel(level);
                    dataFieldGroupDTOList.add(dataFieldGroupDto);
                    findSortedChildGroups(dataFieldGroupDto, level, dataFieldGroupDTOList);
                }
            });
            return dataFieldGroupDTOList.stream().sorted(Comparator.comparing(DataFieldGroupDTO::getGroupLevel)).collect(Collectors.toList());
        }
    }

    private void findSortedChildGroups(DataFieldGroupDTO parentDataFieldGroupDTO, int level, List<DataFieldGroupDTO> dataFieldGroupDTOList) {
        level++;
        List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_PARENT_GROUP_ID, parentDataFieldGroupDTO.getGroupId()))
                .build());
        if (CollectionUtils.isNotEmpty(standardGroupDTOList)) {
            int finalLevel = level;
            standardGroupDTOList.forEach(standardGroupDTO -> {
                DataFieldGroupDTO dataFieldGroupDTO = new DataFieldGroupDTO();
                BeanUtils.copyProperties(standardGroupDTO, dataFieldGroupDTO);
                dataFieldGroupDTO.setGroupLevel(finalLevel);
                //子目录数据标准列表
                List<DataFieldDTO> dataFieldDTOList = dataFieldMapper.list(DataFieldDTO.builder().groupArrays(new Long[]{dataFieldGroupDTO.getGroupId()}).build());
                dataFieldDTOList.forEach(dataFieldDTO -> {
                    //查询标准组
                    List<StandardRelation> standardRelations = standardRelationRepository.select(StandardRelation.builder().fieldStandardId(dataFieldDTO.getFieldId()).build());
                    List<Long> standardTeamIds = standardRelations.stream()
                            .map(StandardRelation::getStandardTeamId)
                            .collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(standardTeamIds)) {
                        List<StandardTeamDTO> standardTeamDTOS = standardTeamRepository.selectDTOByIds(standardTeamIds);
                        //标准组导出
                        dataFieldDTO.setStandardTeamCode(StringUtils.join(standardTeamDTOS.stream().map(StandardTeamDTO::getStandardTeamCode).toArray(), ";"));
                    }
                });
                if (DataSecurityHelper.isTenantOpen() && CollectionUtils.isNotEmpty(dataFieldDTOList)) {
                    dataFieldDTOList.forEach(dataFieldDTO -> {
                        if (StringUtils.isNotEmpty(dataFieldDTO.getChargeName())) {
                            dataFieldDTO.setChargeName(DataSecurityHelper.decrypt(dataFieldDTO.getChargeName()));
                        }
                        if (StringUtils.isNotEmpty(dataFieldDTO.getChargeTel())) {
                            dataFieldDTO.setChargeTel(DataSecurityHelper.decrypt(dataFieldDTO.getChargeTel()));
                        }
                        if (StringUtils.isNotEmpty(dataFieldDTO.getChargeEmail())) {
                            dataFieldDTO.setChargeEmail(DataSecurityHelper.decrypt(dataFieldDTO.getChargeEmail()));
                        }
                        if (StringUtils.isNotEmpty(dataFieldDTO.getChargeDeptName())) {
                            dataFieldDTO.setChargeDeptName(DataSecurityHelper.decrypt(dataFieldDTO.getChargeDeptName()));
                        }
                    });
                }
                dataFieldGroupDTO.setDataFieldDTOList(dataFieldDTOList);
                //设置父分组code
                dataFieldGroupDTO.setParentGroupCode(parentDataFieldGroupDTO.getGroupCode());
                dataFieldGroupDTOList.add(dataFieldGroupDTO);
                findSortedChildGroups(dataFieldGroupDTO, finalLevel, dataFieldGroupDTOList);
            });
        }
    }

    private void findParentGroups(Long groupId, List<DataFieldGroupDTO> standardGroups, int level) {
        DataFieldGroupDTO dataFieldGroupDTO = new DataFieldGroupDTO();
        List<StandardGroupDTO> standardGroupDTOList = standardGroupRepository.selectDTOByCondition(Condition.builder(StandardGroup.class).andWhere(Sqls.custom()
                        .andEqualTo(StandardGroup.FIELD_GROUP_ID, groupId))
                .build());
        level++;
        if (CollectionUtils.isNotEmpty(standardGroupDTOList)) {
            int finalLevel = level;
            standardGroupDTOList.forEach(parentStandardGroupDTO -> {
                BeanUtils.copyProperties(parentStandardGroupDTO, dataFieldGroupDTO);
                //获取设置当前分组的父分组编码
                if (ObjectUtils.isNotEmpty(dataFieldGroupDTO.getGroupId())) {
                    StandardGroupDTO parentGroupDTO = standardGroupRepository.selectDTOByPrimaryKey(dataFieldGroupDTO.getGroupId());
                    dataFieldGroupDTO.setGroupLevel(finalLevel);
                    standardGroups.add(dataFieldGroupDTO);
                    if (ObjectUtils.isNotEmpty(parentGroupDTO.getParentGroupId())) {
                        StandardGroup group = standardGroupRepository.selectByPrimaryKey(parentGroupDTO.getParentGroupId());
                        dataFieldGroupDTO.setParentGroupCode(group.getGroupCode());
                        findParentGroups(parentGroupDTO.getParentGroupId(), standardGroups, finalLevel);
                    }
                }
            });
        }
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
    public void onlineWorkflowCallback(Long fieldId,String nodeApproveResult) {
        nodeApproveResult = (String) dataFieldOnlineWorkflowAdapter.callBack(fieldId,nodeApproveResult);
        if(WorkflowConstant.ApproveAction.APPROVED.equals(nodeApproveResult)){
            workflowing(DetailsHelper.getUserDetails().getTenantId(), fieldId, ONLINE);
        }else {
            workflowing(DetailsHelper.getUserDetails().getTenantId(), fieldId, OFFLINE);
        }
    }

    @Override
    public void offlineWorkflowCallback(Long fieldId,String nodeApproveResult) {
        nodeApproveResult = (String) dataFieldOfflineWorkflowAdapter.callBack(fieldId,nodeApproveResult);
        if(WorkflowConstant.ApproveAction.APPROVED.equals(nodeApproveResult)){
            workflowing(DetailsHelper.getUserDetails().getTenantId(), fieldId, OFFLINE);
        }else {
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

        List<AimStatisticsDTO> insertAimStatisticsDTOS = new CopyOnWriteArrayList<>();
        List<AimStatistics> updateAimStatistics = new CopyOnWriteArrayList<>();

        CountDownLatch countDownLatch = new CountDownLatch(standardAimDTOS.size());
        for (StandardAimDTO aimDTO : standardAimDTOS) {
            CustomThreadPool.getExecutor().submit(() -> {
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
                    int count = aimStatisticsRepository.selectCount(AimStatistics.builder().aimId(aimStatisticsDTO.getAimId()).build());
                    if (count > 0) {
                        //已经统计过，更新统计
                        List<AimStatistics> aimStatisticList = aimStatisticsRepository.select(AimStatistics.builder().aimId(aimStatisticsDTO.getAimId()).build());
                        if (CollectionUtils.isNotEmpty(aimStatisticList)) {
                            AimStatistics oldAimStatistic = aimStatisticList.get(0);
                            AimStatistics newAimStatistic = aimStatisticsConverter.dtoToEntity(aimStatisticsDTO);
                            BeanUtils.copyProperties(oldAimStatistic, newAimStatistic, AimStatistics.FIELD_ROW_NUM,
                                    AimStatistics.FIELD_NON_NULL_ROW, AimStatistics.FIELD_COMPLIANT_ROW,
                                    AimStatistics.FIELD_COMPLIANT_RATE, AimStatistics.FIELD_ACOMPLIANT_RATE);
                            updateAimStatistics.add(newAimStatistic);
                        }
                    } else {
                        insertAimStatisticsDTOS.add(aimStatisticsDTO);
                    }
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
            log.error("error", e);
        }
        // 落标总数统计
        if (CollectionUtils.isNotEmpty(insertAimStatisticsDTOS)) {
            aimStatisticsRepository.batchInsertDTOSelective(insertAimStatisticsDTOS);
        }
        if (CollectionUtils.isNotEmpty(updateAimStatistics)) {
            aimStatisticsRepository.batchUpdateByPrimaryKey(updateAimStatistics);
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
            dataFieldDTO.setStandardStatus(status);
            dataFieldRepository.updateDTOOptional(dataFieldDTO, DataStandard.FIELD_STANDARD_STATUS);
            if (ONLINE.equals(status)) {
                doVersion(dataFieldDTO);
            }
        }
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
