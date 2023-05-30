package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchPlanRelTableDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanRelTable;
import com.hand.hdsp.quality.domain.repository.BatchPlanRelTableRepository;
import com.hand.hdsp.quality.infra.mapper.BatchPlanRelTableMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>批数据方案-表间规则表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Component
public class BatchPlanRelTableRepositoryImpl extends BaseRepositoryImpl<BatchPlanRelTable, BatchPlanRelTableDTO> implements BatchPlanRelTableRepository {

    private final BatchPlanRelTableMapper batchPlanRelTableMapper;

    public BatchPlanRelTableRepositoryImpl(BatchPlanRelTableMapper batchPlanRelTableMapper) {
        this.batchPlanRelTableMapper = batchPlanRelTableMapper;
    }

    @Override
    public int deleteByPlanBaseId(Long planBaseId) {
        return batchPlanRelTableMapper.deleteByPlanBaseId(planBaseId);
    }

    @Override
    public List<BatchPlanRelTableDTO> selectRelTable(BatchPlanRelTableDTO batchPlanRelTableDTO) {
        return batchPlanRelTableMapper.selectRelTable(batchPlanRelTableDTO);

    }
}
