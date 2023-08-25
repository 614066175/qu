package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.BatchPlanTableLineDTO;
import org.xdsp.quality.domain.entity.BatchPlanTableLine;
import org.xdsp.quality.domain.repository.BatchPlanTableLineRepository;
import org.xdsp.quality.infra.mapper.BatchPlanTableLineMapper;

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
