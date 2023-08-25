package org.xdsp.quality.infra.repository.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.PlanGroupDTO;
import org.xdsp.quality.domain.entity.BatchPlanBase;
import org.xdsp.quality.domain.entity.PlanBaseAssign;
import org.xdsp.quality.domain.entity.PlanGroup;
import org.xdsp.quality.domain.repository.BatchPlanBaseRepository;
import org.xdsp.quality.domain.repository.PlanBaseAssignRepository;
import org.xdsp.quality.domain.repository.PlanGroupRepository;
import org.xdsp.quality.infra.constant.GroupType;
import org.xdsp.quality.infra.mapper.PlanGroupMapper;
import org.xdsp.quality.infra.vo.PlanGroupTreeVO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>评估方案分组表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Component
public class PlanGroupRepositoryImpl extends BaseRepositoryImpl<PlanGroup, PlanGroupDTO> implements PlanGroupRepository {

    private final PlanGroupMapper planGroupMapper;
    private final PlanBaseAssignRepository planBaseAssignRepository;
    private final BatchPlanBaseRepository batchPlanBaseRepository;

    public PlanGroupRepositoryImpl(PlanGroupMapper planGroupMapper, PlanBaseAssignRepository planBaseAssignRepository, BatchPlanBaseRepository batchPlanBaseRepository) {
        this.planGroupMapper = planGroupMapper;
        this.planBaseAssignRepository = planBaseAssignRepository;
        this.batchPlanBaseRepository = batchPlanBaseRepository;
    }

    @Override
    public List<PlanGroupTreeVO> tree(PlanGroupTreeVO planGroupTreeVO) {
        List<PlanGroupTreeVO> planGroupTreeVOList;
        if (GroupType.BATCH.equals(planGroupTreeVO.getGroupType())) {
            planGroupTreeVOList = planGroupMapper.treeBatch(planGroupTreeVO);
        } else {
            planGroupTreeVOList = planGroupMapper.treeStream(planGroupTreeVO);
        }
        planGroupTreeVOList.add(PlanGroupTreeVO.ROOT_PLAN_GROUP);
        //如果是分配查询，需要传递质检项的id
        if (planGroupTreeVO.getPlanBaseId() != null) {
            //查询此质检项的创建方案，以及被分配的方案
            List<PlanBaseAssign> planBaseAssigns = planBaseAssignRepository.select(PlanBaseAssign.builder().planBaseId(planGroupTreeVO.getPlanBaseId()).build());
            List<Long> assignPlanId = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(planBaseAssigns)) {
                assignPlanId = planBaseAssigns.stream().map(PlanBaseAssign::getPlanId).collect(Collectors.toList());
            }
            BatchPlanBase batchPlanBase = batchPlanBaseRepository.selectByPrimaryKey(planGroupTreeVO.getPlanBaseId());
            //过滤出plan，进行判断
            List<Long> finalAssignPlanId = assignPlanId;
            planGroupTreeVOList.stream()
                    .filter(groupTreeVO -> "plan".equalsIgnoreCase(groupTreeVO.getType()))
                    .forEach(planVO -> {
                        //如果包含
                        if (finalAssignPlanId.contains(planVO.getId())) {
                            planVO.setAssignedFlag(1);
                            planVO.setEditFlag(1);
                        }//如果是创建方案
                        else if (batchPlanBase.getPlanId().equals(planVO.getId())) {
                            planVO.setAssignedFlag(1);
                            planVO.setEditFlag(0);
                        } else {
                            planVO.setAssignedFlag(0);
                            planVO.setEditFlag(1);
                        }
                    });
        }
        return planGroupTreeVOList;
    }
}
