package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.BatchPlanRelTableDTO;
import org.xdsp.quality.domain.entity.BatchPlanRelTable;
import org.xdsp.quality.domain.repository.BatchPlanRelTableRepository;
import org.xdsp.quality.infra.mapper.BatchPlanRelTableMapper;

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
