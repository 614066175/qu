package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.BatchPlanDTO;
import com.hand.hdsp.quality.api.dto.PlanGroupDTO;
import com.hand.hdsp.quality.app.service.PlanGroupService;
import com.hand.hdsp.quality.domain.entity.BatchPlan;
import com.hand.hdsp.quality.domain.entity.PlanGroup;
import com.hand.hdsp.quality.domain.repository.BatchPlanRepository;
import com.hand.hdsp.quality.domain.repository.PlanGroupRepository;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>评估方案分组表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Service
public class PlanGroupServiceImpl implements PlanGroupService {

    private PlanGroupRepository planGroupRepository;
    private BatchPlanRepository batchPlanRepository;

    public PlanGroupServiceImpl(PlanGroupRepository planGroupRepository, BatchPlanRepository batchPlanRepository) {
        this.planGroupRepository = planGroupRepository;
        this.batchPlanRepository = batchPlanRepository;
    }

    @Override
    public int delete(PlanGroupDTO planGroupDTO) {
        List<PlanGroupDTO> planGroupDTOList = planGroupRepository.selectDTO(PlanGroup.FIELD_PARENT_GROUP_ID, planGroupDTO.getGroupId());
        List<BatchPlanDTO> batchPlanDTOList = batchPlanRepository.selectDTO(BatchPlan.FIELD_GROUP_ID, planGroupDTO.getGroupId());
        if (!planGroupDTOList.isEmpty() || !batchPlanDTOList.isEmpty()) {
            throw new CommonException(ErrorCode.CAN_NOT_DELETE);
        }
        return planGroupRepository.deleteByPrimaryKey(planGroupDTO);
    }
}
