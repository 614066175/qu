package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchPlanTableLineDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanTableLine;
import com.hand.hdsp.quality.domain.repository.BatchPlanTableLineRepository;
import com.hand.hdsp.quality.infra.mapper.BatchPlanTableLineMapper;
import org.springframework.stereotype.Component;

/**
 * <p>批数据方案-表级规则校验项表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Component
public class BatchPlanTableLineRepositoryImpl extends BaseRepositoryImpl<BatchPlanTableLine, BatchPlanTableLineDTO> implements BatchPlanTableLineRepository {

    private final BatchPlanTableLineMapper batchPlanTableLineMapper;

    public BatchPlanTableLineRepositoryImpl(BatchPlanTableLineMapper batchPlanTableLineMapper) {
        this.batchPlanTableLineMapper = batchPlanTableLineMapper;
    }

    @Override
    public int deleteByPlanRuleId(Long planRuleId) {
        return batchPlanTableLineMapper.deleteByPlanRuleId(planRuleId);
    }

    @Override
    public int deleteByPlanBaseId(Long planBaseId) {
        return batchPlanTableLineMapper.deleteByPlanBaseId(planBaseId);
    }
}
