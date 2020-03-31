package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.PlanGroupDTO;
import com.hand.hdsp.quality.domain.entity.PlanGroup;
import com.hand.hdsp.quality.domain.repository.PlanGroupRepository;
import com.hand.hdsp.quality.infra.mapper.PlanGroupMapper;
import com.hand.hdsp.quality.infra.vo.PlanGroupTreeVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>评估方案分组表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Component
public class PlanGroupRepositoryImpl extends BaseRepositoryImpl<PlanGroup, PlanGroupDTO> implements PlanGroupRepository {

    private PlanGroupMapper planGroupMapper;

    public PlanGroupRepositoryImpl(PlanGroupMapper planGroupMapper) {
        this.planGroupMapper = planGroupMapper;
    }

    @Override
    public List<PlanGroupTreeVO> treeBatch(PlanGroupTreeVO planGroupTreeVO) {
        return planGroupMapper.treeBatch(planGroupTreeVO);
    }

    @Override
    public List<PlanGroupTreeVO> treeStream(PlanGroupTreeVO planGroupTreeVO) {
        return planGroupMapper.treeStream(planGroupTreeVO);
    }
}
