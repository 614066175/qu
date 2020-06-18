package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchPlanDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlan;
import com.hand.hdsp.quality.domain.entity.PlanGroup;
import com.hand.hdsp.quality.domain.repository.BatchPlanRepository;
import com.hand.hdsp.quality.domain.repository.PlanGroupRepository;
import com.hand.hdsp.quality.infra.constant.GroupType;
import org.apache.commons.lang3.StringUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

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

    public BatchPlanRepositoryImpl(PlanGroupRepository planGroupRepository) {
        this.planGroupRepository = planGroupRepository;
    }

    @Override
    public List<PlanGroup> listByGroup(BatchPlanDTO batchPlanDTO) {
        @NotNull Long tenantId = batchPlanDTO.getTenantId();
        if (StringUtils.isEmpty(batchPlanDTO.getPlanName())) {
            List<PlanGroup> all = planGroupRepository.select(PlanGroup.builder().groupType(GroupType.BATCH).tenantId(tenantId).build());
            all.add(PlanGroup.ROOT_PLAN_GROUP);
            return all;
        }
        List<BatchPlan> batchPlans = this.selectByCondition(
                Condition.builder(BatchPlan.class)
                        .where(Sqls.custom().andLike(BatchPlan.FIELD_PLAN_NAME, batchPlanDTO.getPlanName(), true)
                                .andEqualTo(BatchPlan.FIELD_TENANT_ID, tenantId))
                        .build()
        );
        if (batchPlans.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> groupIds = batchPlans.stream().map(BatchPlan::getGroupId).collect(Collectors.toList());
        List<PlanGroup> planGroups = planGroupRepository.selectByCondition(
                Condition.builder(PlanGroup.class)
                        .where(Sqls.custom()
                                .andIn(PlanGroup.FIELD_GROUP_ID, groupIds)
                                .andEqualTo(PlanGroup.FIELD_GROUP_TYPE, GroupType.BATCH)
                                .andEqualTo(PlanGroup.FIELD_TENANT_ID, tenantId))
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

    private void getGroup(Long parentId, List<PlanGroup> groups) {
        if (parentId == 0 || parentId == null) {
            return;
        }
        PlanGroup planGroup1 = planGroupRepository.selectByPrimaryKey(parentId);
        groups.add(planGroup1);
        getGroup(planGroup1.getParentGroupId(), groups);
    }

}
