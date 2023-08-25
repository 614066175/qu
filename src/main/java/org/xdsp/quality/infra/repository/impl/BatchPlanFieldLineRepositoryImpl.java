package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.BatchPlanFieldLineDTO;
import org.xdsp.quality.domain.entity.BatchPlanFieldLine;
import org.xdsp.quality.domain.repository.BatchPlanFieldLineRepository;
import org.xdsp.quality.infra.mapper.BatchPlanFieldLineMapper;

/**
 * <p>批数据方案-字段规则校验项表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Component
public class BatchPlanFieldLineRepositoryImpl extends BaseRepositoryImpl<BatchPlanFieldLine, BatchPlanFieldLineDTO> implements BatchPlanFieldLineRepository {

    private final BatchPlanFieldLineMapper batchPlanFieldLineMapper;

    public BatchPlanFieldLineRepositoryImpl(BatchPlanFieldLineMapper batchPlanFieldLineMapper) {
        this.batchPlanFieldLineMapper = batchPlanFieldLineMapper;
    }

    @Override
    public int deleteByParentId(Long planRuleId) {
        return batchPlanFieldLineMapper.deleteByParentId(planRuleId);
    }

    @Override
    public int deleteByPlanBaseId(Long planBaseId) {
        return batchPlanFieldLineMapper.deleteByPlanBaseId(planBaseId);
    }
}
