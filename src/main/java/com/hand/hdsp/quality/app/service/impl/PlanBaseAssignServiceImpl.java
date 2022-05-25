package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.PlanBaseAssignDTO;
import com.hand.hdsp.quality.app.service.PlanBaseAssignService;
import com.hand.hdsp.quality.domain.entity.PlanBaseAssign;
import com.hand.hdsp.quality.domain.repository.PlanBaseAssignRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>质检项分配表应用服务默认实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-20 15:36:10
 */
@Service
public class PlanBaseAssignServiceImpl implements PlanBaseAssignService {

    private final PlanBaseAssignRepository planBaseAssignRepository;

    public PlanBaseAssignServiceImpl(PlanBaseAssignRepository planBaseAssignRepository) {
        this.planBaseAssignRepository = planBaseAssignRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<PlanBaseAssignDTO> baseAssign(Long planBaseId, List<PlanBaseAssignDTO> planBaseAssignDTOList) {
        //删除旧的分配关系，重新维护新得分配关系
        List<PlanBaseAssign> planBaseAssignList = planBaseAssignRepository.select(PlanBaseAssign.builder().planBaseId(planBaseId).build());
        planBaseAssignRepository.batchDeleteByPrimaryKey(planBaseAssignList);
        //重新维护
        planBaseAssignRepository.batchInsertDTOSelective(planBaseAssignDTOList);
        return planBaseAssignDTOList;
    }
}
