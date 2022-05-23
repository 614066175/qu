package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchPlanBaseDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlan;
import com.hand.hdsp.quality.domain.entity.BatchPlanBase;
import com.hand.hdsp.quality.domain.entity.PlanGroup;
import com.hand.hdsp.quality.domain.repository.BatchPlanBaseRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanRepository;
import com.hand.hdsp.quality.domain.repository.PlanGroupRepository;
import com.hand.hdsp.quality.infra.mapper.BatchPlanBaseMapper;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>批数据方案-基础配置表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Component
public class BatchPlanBaseRepositoryImpl extends BaseRepositoryImpl<BatchPlanBase, BatchPlanBaseDTO> implements BatchPlanBaseRepository {

    private final BatchPlanBaseMapper batchPlanBaseMapper;

    public BatchPlanBaseRepositoryImpl(BatchPlanBaseMapper batchPlanBaseMapper) {
        this.batchPlanBaseMapper = batchPlanBaseMapper;

    }

    @Override
    public Page<BatchPlanBaseDTO> list(PageRequest pageRequest, BatchPlanBaseDTO batchPlanBaseDTO) {
        //如果没有点击具体的评估方案，则查询分组下所有评估方案的质检项
        if (batchPlanBaseDTO.getPlanId() == null && batchPlanBaseDTO.getGroupId() != null) {
            //递归查询此分组下所有子分组
            List<PlanGroup> subGroupList = getSubGroup(batchPlanBaseDTO.getGroupId());
            PlanGroupRepository planGroupRepository = ApplicationContextHelper.getContext().getBean(PlanGroupRepository.class);
            //并加上自己
            PlanGroup planGroup = planGroupRepository.selectByPrimaryKey(batchPlanBaseDTO.getGroupId());
            //所有分组（groupId=0）无需考虑
            if (planGroup != null) {
                subGroupList.add(planGroup);
            }
            //查询所有分组下的评估方案
            List<Long> groupIds = subGroupList.stream().map(PlanGroup::getGroupId).collect(Collectors.toList());
            BatchPlanRepository batchPlanRepository = ApplicationContextHelper.getContext().getBean(BatchPlanRepository.class);
            List<BatchPlanDTO> batchPlanDTOS = batchPlanRepository.selectDTOByCondition(Condition.builder(BatchPlan.class)
                    .andWhere(Sqls.custom()
                            .andIn(BatchPlan.FIELD_GROUP_ID, groupIds)
                            .andEqualTo(BatchPlan.FIELD_TENANT_ID, batchPlanBaseDTO.getTenantId())
                            .andEqualTo(BatchPlan.FIELD_PROJECT_ID, batchPlanBaseDTO.getProjectId()))
                    .build());
            if (CollectionUtils.isEmpty(batchPlanDTOS)) {
                return new Page<>();
            }
            List<Long> planIds = batchPlanDTOS.stream().map(BatchPlanDTO::getPlanId).collect(Collectors.toList());
            //查询所有评估方案的质检项
            return PageHelper.doPage(pageRequest,
                    () -> this.selectDTOByCondition(Condition.builder(BatchPlanBase.class)
                            .andWhere(Sqls.custom()
                                    .andIn(BatchPlanBase.FIELD_PLAN_ID, planIds)
                                    .andEqualTo(BatchPlanBase.FIELD_TENANT_ID, batchPlanBaseDTO.getTenantId())
                                    .andEqualTo(BatchPlanBase.FIELD_PROJECT_ID, batchPlanBaseDTO.getProjectId()))
                            .build()));
        }
        return PageHelper.doPage(pageRequest, () -> batchPlanBaseMapper.list(batchPlanBaseDTO));
    }

    /**
     * 获取子分组
     *
     * @param groupId
     * @return
     */
    private List<PlanGroup> getSubGroup(Long groupId) {
        PlanGroupRepository planGroupRepository = ApplicationContextHelper.getContext().getBean(PlanGroupRepository.class);
        List<PlanGroup> subGroupList = planGroupRepository.select(PlanGroup.builder()
                .parentGroupId(groupId).build());
        List<PlanGroup> allSubGroupList = new ArrayList<>(subGroupList);
        if (CollectionUtils.isNotEmpty(subGroupList)) {
            subGroupList.forEach(subGroup -> {
                List<PlanGroup> subPlanGroups = getSubGroup(subGroup.getGroupId());
                allSubGroupList.addAll(subPlanGroups);
            });
        }
        return allSubGroupList;
    }

    @Override
    public BatchPlanBaseDTO detail(Long planBaseId) {
        return batchPlanBaseMapper.detail(planBaseId);
    }
}
