package org.xdsp.quality.infra.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.StreamingPlanDTO;
import org.xdsp.quality.domain.entity.PlanGroup;
import org.xdsp.quality.domain.entity.StreamingPlan;
import org.xdsp.quality.domain.repository.PlanGroupRepository;
import org.xdsp.quality.domain.repository.StreamingPlanRepository;
import org.xdsp.quality.infra.constant.GroupType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>实时数据评估方案表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Component
public class StreamingPlanRepositoryImpl extends BaseRepositoryImpl<StreamingPlan, StreamingPlanDTO> implements StreamingPlanRepository {

    private final PlanGroupRepository planGroupRepository;

    public StreamingPlanRepositoryImpl(PlanGroupRepository planGroupRepository) {
        this.planGroupRepository = planGroupRepository;
    }

    @Override
    public List<PlanGroup> getGroupByPlanName(StreamingPlanDTO streamingPlanDTO) {
        if (StringUtils.isEmpty(streamingPlanDTO.getPlanName())) {
            List<PlanGroup> all = planGroupRepository.select(PlanGroup.builder()
                    .groupType(GroupType.STREAMING)
                    .projectId(streamingPlanDTO.getProjectId())
                    .build());
            all.add(PlanGroup.ROOT_PLAN_GROUP);
            return all;
        }
        List<StreamingPlan> streamingPlans = this.selectByCondition(
                Condition.builder(StreamingPlan.class)
                        .where(Sqls.custom().andLike(StreamingPlan.FIELD_PLAN_NAME, streamingPlanDTO.getPlanName(), true))
                        .build()
        );
        if (streamingPlans.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> groupIds = streamingPlans.stream().map(StreamingPlan::getGroupId).collect(Collectors.toList());
        List<PlanGroup> planGroups = planGroupRepository.selectByCondition(
                Condition.builder(PlanGroup.class)
                        .where(Sqls.custom()
                                .andIn(PlanGroup.FIELD_GROUP_ID, groupIds)
                                .andEqualTo(PlanGroup.FIELD_GROUP_TYPE, GroupType.STREAMING))
                        .build()
        );
        List<PlanGroup> groups = new ArrayList<>();
        planGroups.forEach(p -> {
            getGroup(p.getParentGroupId(), groups);
            groups.add(p);
        });
        planGroups.add(PlanGroup.ROOT_PLAN_GROUP);
        return planGroups;
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
