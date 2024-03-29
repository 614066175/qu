package org.xdsp.quality.infra.repository.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.BatchPlanDTO;
import org.xdsp.quality.domain.entity.BatchPlan;
import org.xdsp.quality.domain.entity.PlanGroup;
import org.xdsp.quality.domain.repository.BatchPlanRepository;
import org.xdsp.quality.domain.repository.PlanGroupRepository;
import org.xdsp.quality.infra.constant.GroupType;
import org.xdsp.quality.infra.mapper.PlanGroupMapper;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>批数据评估方案表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Component
public class BatchPlanRepositoryImpl extends BaseRepositoryImpl<BatchPlan, BatchPlanDTO> implements BatchPlanRepository {

    private final PlanGroupRepository planGroupRepository;
    private final PlanGroupMapper planGroupMapper;

    public BatchPlanRepositoryImpl(PlanGroupRepository planGroupRepository, PlanGroupMapper planGroupMapper) {
        this.planGroupRepository = planGroupRepository;
        this.planGroupMapper = planGroupMapper;
    }

    @Override
    public List<PlanGroup> listByGroup(BatchPlanDTO batchPlanDTO) {
        @NotNull Long tenantId = batchPlanDTO.getTenantId();
        Long projectId = batchPlanDTO.getProjectId();
        if (StringUtils.isEmpty(batchPlanDTO.getPlanName())) {
            //查询这个项目下的以及共享到这个项目下的分组
            List<PlanGroup> planGroups = planGroupMapper.ownAndShareGroup(PlanGroup.builder()
                    .groupType(GroupType.BATCH).projectId(projectId).tenantId(tenantId).build());
//            List<PlanGroup> all = planGroupRepository.select(PlanGroup.builder()
//                    .groupType(GroupType.BATCH).projectId(projectId).tenantId(tenantId).build());
            planGroups.add(PlanGroup.ROOT_PLAN_GROUP);
            return planGroups;
        }
        List<BatchPlan> batchPlans = this.selectByCondition(
                Condition.builder(BatchPlan.class)
                        .where(Sqls.custom().andLike(BatchPlan.FIELD_PLAN_NAME, batchPlanDTO.getPlanName(), true)
                                .andEqualTo(BatchPlan.FIELD_PROJECT_ID, projectId)
                                .andEqualTo(BatchPlan.FIELD_TENANT_ID, tenantId))
                        .build()
        );
        if (batchPlans.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> groupIds = batchPlans.stream().map(BatchPlan::getGroupId).collect(Collectors.toList());
        //通过主键查即可
        List<PlanGroup> planGroups = planGroupRepository.selectByCondition(
                Condition.builder(PlanGroup.class)
                        .where(Sqls.custom()
                                .andIn(PlanGroup.FIELD_GROUP_ID, groupIds))
                        .build()
        );
        List<PlanGroup> groups = new ArrayList<>();
        planGroups.forEach(p -> {
            getGroup(p.getParentGroupId(), groups);
            groups.add(p);
        });
        groups.add(PlanGroup.ROOT_PLAN_GROUP);
        return groups;
    }

    @Override
    public void batchImport(List<BatchPlanDTO> batchPlanDTOList) {

    }

    @Override
    public void clearJobName(String jobName, Long tenantId, Long projectId) {
        List<BatchPlanDTO> batchPlanDTOS = this.selectDTOByCondition(Condition.builder(BatchPlan.class)
                .andWhere(Sqls.custom().andEqualTo(BatchPlan.FIELD_PLAN_JOB_CODE, jobName)
                        .andEqualTo(BatchPlan.FIELD_TENANT_ID, tenantId)
                        .andEqualTo(BatchPlan.FIELD_PROJECT_ID, projectId))
                .build());
        if (CollectionUtils.isNotEmpty(batchPlanDTOS)) {
            batchPlanDTOS.forEach(batchPlanDTO -> {
                //清空任务名
                batchPlanDTO.setPlanJobCode(null);
                this.updateDTOAllColumnWhereTenant(batchPlanDTO, tenantId);
            });
        }
    }

    private void getGroup(Long parentId, List<PlanGroup> groups) {
        if (parentId == null || parentId == 0) {
            return;
        }
        PlanGroup planGroup1 = planGroupRepository.selectByPrimaryKey(parentId);
        groups.add(planGroup1);
        getGroup(planGroup1.getParentGroupId(), groups);
    }

}
