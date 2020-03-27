package com.hand.hdsp.quality.infra.repository.impl;

import java.util.List;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchPlanDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlan;
import com.hand.hdsp.quality.domain.repository.BatchPlanRepository;
import com.hand.hdsp.quality.infra.mapper.BatchPlanMapper;
import org.springframework.stereotype.Component;

/**
 * <p>批数据评估方案表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Component
public class BatchPlanRepositoryImpl extends BaseRepositoryImpl<BatchPlan, BatchPlanDTO> implements BatchPlanRepository {

    private final BatchPlanMapper batchPlanMapper;

    public BatchPlanRepositoryImpl(BatchPlanMapper batchPlanMapper) {
        this.batchPlanMapper = batchPlanMapper;
    }

    @Override
    public List<BatchPlanDTO> listByGroup(BatchPlanDTO batchPlanDTO) {
        return batchPlanMapper.getGroupByPlanName(batchPlanDTO);
    }
}
