package com.hand.hdsp.quality.infra.repository.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StreamingPlanDTO;
import com.hand.hdsp.quality.domain.entity.PlanGroup;
import com.hand.hdsp.quality.domain.entity.StreamingPlan;
import com.hand.hdsp.quality.domain.repository.PlanGroupRepository;
import com.hand.hdsp.quality.domain.repository.StreamingPlanRepository;
import com.hand.hdsp.quality.infra.mapper.StreamingPlanMapper;
import io.choerodon.core.exception.CommonException;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

/**
 * <p>实时数据评估方案表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Component
public class StreamingPlanRepositoryImpl extends BaseRepositoryImpl<StreamingPlan, StreamingPlanDTO> implements StreamingPlanRepository {

    private final StreamingPlanMapper streamingPlanMapper;
    private final PlanGroupRepository planGroupRepository;

    public StreamingPlanRepositoryImpl(StreamingPlanMapper streamingPlanMapper, PlanGroupRepository planGroupRepository) {
        this.streamingPlanMapper = streamingPlanMapper;
        this.planGroupRepository = planGroupRepository;
    }

    @Override
    public List<PlanGroup> getGroupByPlanName(StreamingPlanDTO streamingPlanDTO) {
        List<StreamingPlan> streamingPlans = this.selectByCondition(
                Condition.builder(StreamingPlan.class)
                        .where(Sqls.custom().andLike(StreamingPlan.FIELD_PLAN_NAME, streamingPlanDTO.getPlanName(), true))
                        .build()
        );
        if (streamingPlans.isEmpty()){
            return Collections.emptyList();
        }
        List<Long> groupIds = streamingPlans.stream().map(StreamingPlan::getGroupId).collect(Collectors.toList());
        List<PlanGroup> planGroups = planGroupRepository.selectByCondition(
                Condition.builder(PlanGroup.class)
                        .where(Sqls.custom().andIn(PlanGroup.FIELD_GROUP_ID, groupIds))
                        .build()
        );
        List<PlanGroup> groups = new ArrayList<>();
        planGroups.stream().forEach(p ->{
            getGroup(p.getParentGroupId(),groups);
            groups.add(p);
        });
        planGroups.add(planGroupRepository.selectByPrimaryKey(0L));
        return planGroups;
    }

    private void getGroup(Long parentId,List<PlanGroup> groups){
        if (parentId == 0){
            return;
        }
        PlanGroup planGroup1 = planGroupRepository.selectByPrimaryKey(parentId);
        groups.add(planGroup1);
        getGroup(planGroup1.getParentGroupId(),groups);
    }

}
