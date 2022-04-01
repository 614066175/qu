package com.hand.hdsp.quality.app.service.impl;

import static com.hand.hdsp.quality.infra.constant.GroupType.BATCH;

import java.util.List;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.PlanGroupService;
import com.hand.hdsp.quality.domain.entity.*;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import io.choerodon.core.exception.CommonException;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final BatchPlanFieldRepository batchPlanFieldRepository;
    private final BatchPlanRelTableRepository batchPlanRelTableRepository;

    public PlanGroupServiceImpl(PlanGroupRepository planGroupRepository,
                                BatchPlanRepository batchPlanRepository,
                                BatchPlanBaseRepository batchPlanBaseRepository,
                                BatchPlanTableRepository batchPlanTableRepository,
                                BatchPlanFieldRepository batchPlanFieldRepository,
                                BatchPlanRelTableRepository batchPlanRelTableRepository) {
        this.planGroupRepository = planGroupRepository;
        this.batchPlanRepository = batchPlanRepository;
        this.batchPlanBaseRepository = batchPlanBaseRepository;
        this.batchPlanTableRepository = batchPlanTableRepository;
        this.batchPlanFieldRepository = batchPlanFieldRepository;
        this.batchPlanRelTableRepository = batchPlanRelTableRepository;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(PlanGroupDTO planGroupDTO) {
        List<PlanGroupDTO> planGroupDTOList = planGroupRepository.selectDTO(PlanGroup.FIELD_PARENT_GROUP_ID, planGroupDTO.getGroupId());
        List<BatchPlanDTO> batchPlanDTOList = batchPlanRepository.selectDTO(BatchPlan.FIELD_GROUP_ID, planGroupDTO.getGroupId());
        if (!planGroupDTOList.isEmpty() || !batchPlanDTOList.isEmpty()) {
            throw new CommonException(ErrorCode.CAN_NOT_DELETE);
        }
        return planGroupRepository.deleteByPrimaryKey(planGroupDTO);
    }

    @Override
    public List<PlanGroupDTO> export(PlanGroupDTO dto, ExportParam exportParam) {
        List<PlanGroupDTO> planGroupDTOList = planGroupRepository.selectDTOByCondition(Condition.builder(PlanGroup.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(PlanGroup.FIELD_GROUP_TYPE, BATCH))
                .build());
       planGroupDTOList.forEach(planGroupDTO -> {
           //查询分组下的评估方案
           List<BatchPlanDTO> batchPlanDTOList = batchPlanRepository.selectDTOByCondition(Condition.builder(BatchPlan.class)
                   .andWhere(Sqls.custom()
                           .andEqualTo(BatchPlan.FIELD_PROJECT_ID, planGroupDTO.getProjectId())
                           .andEqualTo(BatchPlan.FIELD_GROUP_ID, planGroupDTO.getGroupId())
                           .andEqualTo(BatchPlan.FIELD_TENANT_ID, planGroupDTO.getTenantId()))
                   .build());
           batchPlanDTOList.forEach(batchPlanDTO -> {
               //查询评估方案下的基础配置
               List<BatchPlanBaseDTO> batchPlanBaseDTOList = batchPlanBaseRepository.selectDTOByCondition(Condition.builder(BatchPlanBase.class)
                       .andWhere(Sqls.custom()
                               .andEqualTo(BatchPlanBase.FIELD_PROJECT_ID, batchPlanDTO.getProjectId())
                               .andEqualTo(BatchPlanBase.FIELD_PLAN_ID, batchPlanDTO.getPlanId())
                               .andEqualTo(BatchPlanBase.FIELD_TENANT_ID, batchPlanDTO.getTenantId()))
                       .build());
                batchPlanBaseDTOList.forEach(batchPlanBaseDTO -> {
                    //查询基础配置下的表级，字段，表间规则
                    List<BatchPlanTableDTO> batchPlanTableDTOList = batchPlanTableRepository.selectDTOByCondition(Condition.builder(BatchPlanTable.class)
                            .andWhere(Sqls.custom()
                                    .andEqualTo(BatchPlanTable.FIELD_PROJECT_ID, batchPlanBaseDTO.getProjectId())
                                    .andEqualTo(BatchPlanTable.FIELD_PLAN_BASE_ID, batchPlanBaseDTO.getPlanBaseId())
                                    .andEqualTo(BatchPlanTable.FIELD_TENANT_ID, batchPlanBaseDTO.getTenantId()))
                            .build());
                    List<BatchPlanFieldDTO> batchPlanFieldDTOList = batchPlanFieldRepository.selectDTOByCondition(Condition.builder(BatchPlanField.class)
                            .andWhere(Sqls.custom()
                                    .andEqualTo(BatchPlanField.FIELD_PROJECT_ID, batchPlanBaseDTO.getProjectId())
                                    .andEqualTo(BatchPlanField.FIELD_PLAN_BASE_ID, batchPlanBaseDTO.getPlanBaseId())
                                    .andEqualTo(BatchPlanField.FIELD_TENANT_ID, batchPlanBaseDTO.getTenantId()))
                            .build());
                    List<BatchPlanRelTableDTO> batchPlanRelTableDTOList = batchPlanRelTableRepository.selectDTOByCondition(Condition.builder(BatchPlanRelTable.class)
                            .andWhere(Sqls.custom()
                                    .andEqualTo(BatchPlanRelTable.FIELD_PROJECT_ID, batchPlanBaseDTO.getProjectId())
                                    .andEqualTo(BatchPlanRelTable.FIELD_PLAN_BASE_ID, batchPlanBaseDTO.getPlanBaseId())
                                    .andEqualTo(BatchPlanRelTable.FIELD_TENANT_ID, batchPlanBaseDTO.getTenantId()))
                            .build());
                    batchPlanBaseDTO.setBatchPlanTableDTOList(batchPlanTableDTOList);
                    batchPlanBaseDTO.setBatchPlanFieldDTOList(batchPlanFieldDTOList);
                    batchPlanBaseDTO.setBatchPlanRelTableDTOList(batchPlanRelTableDTOList);
                });
                batchPlanDTO.setBatchPlanBaseDTOList(batchPlanBaseDTOList);
           });
           planGroupDTO.setBatchPlanDTOList(batchPlanDTOList);
       });
        return planGroupDTOList;
    }
}
