package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchPlanRelTableLineDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanRelTableLine;
import com.hand.hdsp.quality.domain.repository.BatchPlanRelTableLineRepository;
import com.hand.hdsp.quality.infra.mapper.BatchPlanRelTableLineMapper;
import org.springframework.stereotype.Component;

/**
 * <p>批数据方案-表间规则关联关系表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Component
public class BatchPlanRelTableLineRepositoryImpl extends BaseRepositoryImpl<BatchPlanRelTableLine, BatchPlanRelTableLineDTO> implements BatchPlanRelTableLineRepository {

    private final BatchPlanRelTableLineMapper batchPlanRelTableLineMapper;

    public BatchPlanRelTableLineRepositoryImpl(BatchPlanRelTableLineMapper batchPlanRelTableLineMapper) {
        this.batchPlanRelTableLineMapper = batchPlanRelTableLineMapper;
    }

    @Override
    public int deleteByParentId(Long planRelTableId) {
        return batchPlanRelTableLineMapper.deleteByParentId(planRelTableId);
    }
}
