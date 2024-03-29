package org.xdsp.quality.app.service.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xdsp.core.util.ProjectHelper;
import org.xdsp.quality.api.dto.BatchPlanBaseDTO;
import org.xdsp.quality.api.dto.BatchPlanDTO;
import org.xdsp.quality.api.dto.PlanGroupDTO;
import org.xdsp.quality.app.service.PlanGroupService;
import org.xdsp.quality.domain.entity.BatchPlan;
import org.xdsp.quality.domain.entity.BatchPlanBase;
import org.xdsp.quality.domain.entity.PlanGroup;
import org.xdsp.quality.domain.repository.*;
import org.xdsp.quality.infra.constant.ErrorCode;
import org.xdsp.quality.infra.mapper.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.xdsp.quality.infra.constant.GroupType.BATCH;

/**
 * <p>评估方案分组表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Service
public class PlanGroupServiceImpl implements PlanGroupService {

    private final PlanGroupRepository planGroupRepository;
    private final BatchPlanRepository batchPlanRepository;
    private final BatchPlanBaseRepository batchPlanBaseRepository;
    private final BatchPlanTableRepository batchPlanTableRepository;
    private final BatchPlanTableMapper batchPlanTableMapper;
    private final BatchPlanFieldRepository batchPlanFieldRepository;
    private final BatchPlanFieldMapper batchPlanFieldMapper;
    private final BatchPlanRelTableRepository batchPlanRelTableRepository;
    private final BatchPlanRelTableMapper batchPlanRelTableMapper;
    private final BatchPlanBaseMapper batchPlanBaseMapper;
    private final BaseFormValueRepository baseFormValueRepository;
    private final PlanGroupMapper planGroupMapper;

    public PlanGroupServiceImpl(PlanGroupRepository planGroupRepository,
                                BatchPlanRepository batchPlanRepository,
                                BatchPlanBaseRepository batchPlanBaseRepository,
                                BatchPlanTableRepository batchPlanTableRepository,
                                BatchPlanTableMapper batchPlanTableMapper, BatchPlanFieldRepository batchPlanFieldRepository,
                                BatchPlanFieldMapper batchPlanFieldMapper, BatchPlanRelTableRepository batchPlanRelTableRepository,
                                BatchPlanRelTableMapper batchPlanRelTableMapper, BatchPlanBaseMapper batchPlanBaseMapper, BaseFormValueRepository baseFormValueRepository, PlanGroupMapper planGroupMapper) {
        this.planGroupRepository = planGroupRepository;
        this.batchPlanRepository = batchPlanRepository;
        this.batchPlanBaseRepository = batchPlanBaseRepository;
        this.batchPlanTableRepository = batchPlanTableRepository;
        this.batchPlanTableMapper = batchPlanTableMapper;
        this.batchPlanFieldRepository = batchPlanFieldRepository;
        this.batchPlanFieldMapper = batchPlanFieldMapper;
        this.batchPlanRelTableRepository = batchPlanRelTableRepository;
        this.batchPlanRelTableMapper = batchPlanRelTableMapper;
        this.batchPlanBaseMapper = batchPlanBaseMapper;
        this.baseFormValueRepository = baseFormValueRepository;
        this.planGroupMapper = planGroupMapper;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(PlanGroupDTO planGroupDTO) {
        //分组或子分组存在评估方案不可删除;不存在或存在空的分组则删除，并同时删除空的分组
        //遍历获取子目录集合
        List<PlanGroupDTO> planGroupDTOList = new ArrayList<>();
        findChildGroups(planGroupDTO, planGroupDTOList);
        planGroupDTOList.add(planGroupDTO);
        planGroupDTOList.forEach(planGroupDto -> {
            //校验分组下是否存在评估方案
            List<BatchPlan> batchPlans = batchPlanRepository.selectByCondition(Condition.builder(BatchPlan.class).andWhere(Sqls.custom()
                            .andEqualTo(BatchPlan.FIELD_TENANT_ID, planGroupDto.getTenantId())
                            .andEqualTo(BatchPlan.FIELD_PROJECT_ID, planGroupDto.getProjectId())
                            .andEqualTo(BatchPlan.FIELD_GROUP_ID, planGroupDto.getGroupId()))
                    .build());
            if (CollectionUtils.isNotEmpty(batchPlans)) {
                throw new CommonException(ErrorCode.EXISTS_OTHER_PLAN);
            }
        });
        return planGroupRepository.batchDTODeleteByPrimaryKey(planGroupDTOList);
    }

    private void findChildGroups(PlanGroupDTO planGroupDTO, List<PlanGroupDTO> planGroupDTOList) {
        List<PlanGroupDTO> planGroupDTOS = planGroupRepository.selectDTOByCondition(Condition.builder(PlanGroup.class).andWhere(Sqls.custom()
                        .andEqualTo(PlanGroup.FIELD_PARENT_GROUP_ID, planGroupDTO.getGroupId())
                        .andEqualTo(PlanGroup.FIELD_TENANT_ID, planGroupDTO.getTenantId())
                        .andEqualTo(PlanGroup.FIELD_PROJECT_ID, planGroupDTO.getProjectId()))
                .build());
        if (CollectionUtils.isNotEmpty(planGroupDTOS)) {
            planGroupDTOList.addAll(planGroupDTOS);
            planGroupDTOS.forEach(planGroupDto -> {
                findChildGroups(planGroupDto, planGroupDTOList);
            });
        }
    }

    @Override
    @ProcessLovValue(targetField = {"", "batchPlanDTOList", "batchPlanDTOList.batchPlanBaseDTOList", "batchPlanDTOList.batchPlanBaseDTOList.batchPlanTableDTOList", "batchPlanDTOList.batchPlanBaseDTOList.batchPlanFieldDTOList", "batchPlanDTOList.batchPlanBaseDTOList.batchPlanRelTableDTOList"})
    public List<PlanGroupDTO> export(PlanGroupDTO dto, ExportParam exportParam) {
        if (ObjectUtils.isNotEmpty(dto.getPlanBaseId())) {
            BatchPlanBaseDTO batchPlanBaseDTO = batchPlanBaseRepository.selectDTOByPrimaryKey(dto.getPlanBaseId());
            dto.setPlanId(batchPlanBaseDTO.getPlanId());
        }
        //获取groupId
        if (ObjectUtils.isNotEmpty(dto.getPlanId())) {
            BatchPlan batchPlan = batchPlanRepository.selectByPrimaryKey(dto.getPlanId());
            dto.setGroupId(batchPlan.getGroupId());
        }
        List<PlanGroupDTO> planGroupDTOList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(dto.getGroupId()) && dto.getGroupId() == 0 && (ObjectUtils.isNotEmpty(dto.getPlanBaseCode()) || ObjectUtils.isNotEmpty(dto.getPlanBaseName()) || ObjectUtils.isNotEmpty(dto.getObjectName()))) {
            List<BatchPlanBaseDTO> batchPlanBaseDTOS = batchPlanBaseRepository.selectDTOByCondition(Condition.builder(BatchPlanBase.class).andWhere(Sqls.custom()
                            .andEqualTo(BatchPlanBase.FIELD_PLAN_BASE_CODE, dto.getPlanBaseCode(), true)
                            .andEqualTo(BatchPlanBase.FIELD_PLAN_BASE_NAME, dto.getPlanBaseName(), true)
                            .andEqualTo(BatchPlanBase.FIELD_OBJECT_NAME, dto.getObjectName(), true)
                            .andEqualTo(BatchPlanBase.FIELD_TENANT_ID, dto.getTenantId()))
                    .build());
            //获取质检项分组
            List<PlanGroupDTO> finalPlanGroupDTOList = planGroupDTOList;
            batchPlanBaseDTOS.forEach(batchPlanBaseDTO -> {
                Long planId = batchPlanBaseDTO.getPlanId();
                BatchPlan batchPlan = batchPlanRepository.selectByPrimaryKey(planId);
                if (ObjectUtils.isNotEmpty(batchPlan)) {
                    PlanGroupDTO planGroupDTO = planGroupRepository.selectDTOByPrimaryKey(batchPlan.getGroupId());
                    finalPlanGroupDTOList.add(planGroupDTO);
                }
            });
        } else {
            //选中所有分组时传入groupId为0
            if (ObjectUtils.isNotEmpty(dto.getGroupId()) && dto.getGroupId() == 0L) {
                dto.setGroupId(null);
            }
            planGroupDTOList = planGroupRepository.selectDTOByCondition(Condition.builder(PlanGroup.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(PlanGroup.FIELD_GROUP_TYPE, BATCH)
                            .andEqualTo(PlanGroup.FIELD_TENANT_ID, dto.getTenantId())
                            .andEqualTo(PlanGroup.FIELD_PROJECT_ID, ProjectHelper.getProjectId())
                            .andEqualTo(PlanGroup.FIELD_GROUP_ID, dto.getGroupId(), true))
                    .build());
        }
        //获取导出的分组，获取分组下的评估方案，以及质检项
        //递归获取分组的层级
        LinkedHashSet<PlanGroupDTO> exportPlanGroupDTOList = new LinkedHashSet<>();

        planGroupDTOList.forEach(planGroupDTO -> {
            List<PlanGroupDTO> parentGroup = getParentGroup(planGroupDTO.getParentGroupId());
            exportPlanGroupDTOList.addAll(parentGroup);
            if(CollectionUtils.isNotEmpty(parentGroup)){
                //设置当前分组的父分组编码为最后一个结果的值
                planGroupDTO.setParentGroupCode(parentGroup.get(parentGroup.size()-1).getGroupCode());
            }
            //查询分组下的评估方案
            List<BatchPlanDTO> batchPlanDTOList = batchPlanRepository.selectDTOByCondition(Condition.builder(BatchPlan.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(BatchPlan.FIELD_PROJECT_ID, planGroupDTO.getProjectId())
                            .andEqualTo(BatchPlan.FIELD_GROUP_ID, planGroupDTO.getGroupId())
                            .andEqualTo(BatchPlan.FIELD_TENANT_ID, planGroupDTO.getTenantId())
                            .andEqualTo(BatchPlan.FIELD_PLAN_NAME, dto.getPlanName(), true)
                            .andEqualTo(BatchPlan.FIELD_PLAN_ID, dto.getPlanId(), true)
                    )
                    .build());
            batchPlanDTOList.stream().parallel().forEach(batchPlanDTO -> {
                //查询评估方案下的基础配置
                List<BatchPlanBaseDTO> batchPlanBaseDTOList = batchPlanBaseRepository.selectDTOByCondition(Condition.builder(BatchPlanBase.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(BatchPlanBase.FIELD_PROJECT_ID, batchPlanDTO.getProjectId())
                                .andEqualTo(BatchPlanBase.FIELD_PLAN_ID, batchPlanDTO.getPlanId())
                                .andEqualTo(BatchPlanBase.FIELD_TENANT_ID, batchPlanDTO.getTenantId())
                                .andEqualTo(BatchPlanBase.FIELD_PLAN_BASE_CODE, dto.getPlanBaseCode(), true)
                                .andEqualTo(BatchPlanBase.FIELD_PLAN_BASE_NAME, dto.getPlanBaseName(), true)
                                .andEqualTo(BatchPlanBase.FIELD_OBJECT_NAME, dto.getObjectName(), true)
                                .andEqualTo(BatchPlanBase.FIELD_DESCRIPTION, dto.getDescription(), true)
                                .andEqualTo(BatchPlanBase.FIELD_DATASOURCE_SCHEMA, dto.getDatasourceSchema(), true)
                                .andEqualTo(BatchPlanBase.FIELD_PLAN_BASE_ID, dto.getPlanBaseId(), true)
                        )
                        .build());

                //formValue fieldNum tableNum relTableNum currentPlanName
                batchPlanBaseDTOList.forEach(batchPlanBaseDTO -> {
                    //查询基础配置下的动态表单列，表级，字段，表间规则
                    getPlanFormValue(batchPlanBaseDTO);
                    getPlanTable(batchPlanBaseDTO);
                    getPlanField(batchPlanBaseDTO);
                    getPlanRelTable(batchPlanBaseDTO);
                });
                batchPlanDTO.setBatchPlanBaseDTOList(batchPlanBaseDTOList);
            });
            planGroupDTO.setBatchPlanDTOList(batchPlanDTOList);
            exportPlanGroupDTOList.add(planGroupDTO);
        });
        return new ArrayList<>(exportPlanGroupDTOList);
    }

    @Override
    public int create(PlanGroupDTO dto) {

        if (dto.getParentGroupId() == null) {
            dto.setParentGroupId(0L);
        }
        // 校验父目录下是否有标准
//        if (ruleGroupDTO.getParentGroupId() != null) {
//            RuleDTO dto = ruleGroupRepository.selectDTOByPrimaryKey(standardGroupDTO.getParentGroupId());
//            existStandard(dto);
//        }
        // 校验编码存在
        List<PlanGroupDTO> dtoList = planGroupRepository.selectDTOByCondition(Condition.builder(PlanGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(PlanGroup.FIELD_TENANT_ID, dto.getTenantId())
                        .andEqualTo(PlanGroup.FIELD_GROUP_CODE, dto.getGroupCode())
                        .andEqualTo(PlanGroup.FIELD_PROJECT_ID, dto.getProjectId()))
                .build());
        if (CollectionUtils.isNotEmpty(dtoList)) {
            throw new CommonException(ErrorCode.CODE_ALREADY_EXISTS);
        }
        // 校验名称存在
        dtoList = planGroupRepository.selectDTOByCondition(Condition.builder(PlanGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(PlanGroup.FIELD_TENANT_ID, dto.getTenantId())
                        .andEqualTo(PlanGroup.FIELD_GROUP_NAME, dto.getGroupName())
                        .andEqualTo(PlanGroup.FIELD_PROJECT_ID, dto.getProjectId())
                        .andEqualTo(PlanGroup.FIELD_PARENT_GROUP_ID, dto.getParentGroupId()))
                .build());
        if (CollectionUtils.isNotEmpty(dtoList)) {
            throw new CommonException(ErrorCode.GROUP_NAME_ALREADY_EXIST);
        }
        return planGroupRepository.insertDTOSelective(dto);
    }

    private List<PlanGroupDTO> getParentGroup(Long parentGroupId) {
        List<PlanGroupDTO> planGroupDTOS = new ArrayList<>();
        if (parentGroupId != 0) {
            PlanGroupDTO planGroupDTO = planGroupMapper.selectWithParentCode(parentGroupId);
            if (planGroupDTO != null) {
                List<PlanGroupDTO> parentGroup = getParentGroup(planGroupDTO.getParentGroupId());
                planGroupDTOS.addAll(parentGroup);
                planGroupDTOS.add(planGroupDTO);
            }
        }
        return planGroupDTOS;
    }

    private void getPlanFormValue(BatchPlanBaseDTO batchPlanBaseDTO) {
        batchPlanBaseDTO.setBaseFormValueDTOList(baseFormValueRepository.selectByPlanBaseId(batchPlanBaseDTO.getPlanBaseId()));
    }

    private void getPlanTable(BatchPlanBaseDTO batchPlanBaseDTO) {
        batchPlanBaseDTO.setBatchPlanTableDTOList(batchPlanTableMapper.getPlanTable(batchPlanBaseDTO.getPlanBaseId()));
    }

    private void getPlanField(BatchPlanBaseDTO batchPlanBaseDTO) {
        batchPlanBaseDTO.setBatchPlanFieldDTOList(batchPlanFieldMapper.getPlanField(batchPlanBaseDTO.getPlanBaseId()));
    }

    private void getPlanRelTable(BatchPlanBaseDTO batchPlanBaseDTO) {
        batchPlanBaseDTO.setBatchPlanRelTableDTOList(batchPlanRelTableMapper.getRelTable(batchPlanBaseDTO.getPlanBaseId()));
    }
}
