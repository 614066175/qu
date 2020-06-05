package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldLineDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanFieldLine;
import com.hand.hdsp.quality.domain.repository.BatchPlanFieldLineRepository;
import com.hand.hdsp.quality.infra.mapper.BatchPlanFieldLineMapper;
import org.springframework.stereotype.Component;

/**
 * <p>批数据方案-字段规则校验项表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Component
public class BatchPlanFieldLineRepositoryImpl extends BaseRepositoryImpl<BatchPlanFieldLine, BatchPlanFieldLineDTO> implements BatchPlanFieldLineRepository {

    private BatchPlanFieldLineMapper batchPlanFieldLineMapper;

    public BatchPlanFieldLineRepositoryImpl(BatchPlanFieldLineMapper batchPlanFieldLineMapper) {
        this.batchPlanFieldLineMapper = batchPlanFieldLineMapper;
    }

    @Override
    public int deleteByParentId(Long planRuleId) {
        return batchPlanFieldLineMapper.deleteByParentId(planRuleId);
    }
}
