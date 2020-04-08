package com.hand.hdsp.quality.infra.repository.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchPlanDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlan;
import com.hand.hdsp.quality.domain.entity.PlanGroup;
import com.hand.hdsp.quality.domain.repository.BatchPlanRepository;
import com.hand.hdsp.quality.domain.repository.PlanGroupRepository;
import com.hand.hdsp.quality.infra.constant.GroupType;
import io.choerodon.core.exception.CommonException;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

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
        if (batchPlanDTO.getPlanName() == null || batchPlanDTO.getPlanName().length() == 0){
            List<PlanGroup> all = planGroupRepository.select(PlanGroup.builder().groupType(GroupType.BATCH).build());
            all.add(planGroupRepository.selectByPrimaryKey(0L));
            return all;
        }
        List<BatchPlan> batchPlans = this.selectByCondition(
                Condition.builder(BatchPlan.class)
                        .where(Sqls.custom().andLike(BatchPlan.FIELD_PLAN_NAME, batchPlanDTO.getPlanName(), true))
                        .build()
        );
        if (batchPlans.isEmpty()){
            return Collections.emptyList();
        }
        List<Long> groupIds = batchPlans.stream().map(BatchPlan::getGroupId).collect(Collectors.toList());
        List<PlanGroup> planGroups = planGroupRepository.selectByCondition(
                Condition.builder(PlanGroup.class)
                        .where(Sqls.custom()
                                .andIn(PlanGroup.FIELD_GROUP_ID, groupIds)
                                .andEqualTo(PlanGroup.FIELD_GROUP_TYPE, GroupType.BATCH))
                        .build()
        );
        List<PlanGroup> groups = new ArrayList<>();
        planGroups.stream().forEach(p ->{
            getGroup(p.getParentGroupId(),groups);
            groups.add(p);
        });
        groups.add(planGroupRepository.selectByPrimaryKey(0L));
        return groups;
    }

    private void getGroup(Long parentId,List<PlanGroup> groups){
        if (parentId == 0 || parentId == null){
            return;
        }
        PlanGroup planGroup1 = planGroupRepository.selectByPrimaryKey(parentId);
        groups.add(planGroup1);
        getGroup(planGroup1.getParentGroupId(),groups);
    }

}
