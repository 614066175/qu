package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.BatchPlanTableConDTO;
import org.xdsp.quality.domain.entity.BatchPlanTableCon;
import org.xdsp.quality.domain.repository.BatchPlanTableConRepository;
import org.xdsp.quality.infra.dataobject.BatchPlanTableConDO;
import org.xdsp.quality.infra.mapper.BatchPlanTableConMapper;

import java.util.List;

/**
 * <p>批数据方案-表级规则条件表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:03:41
 */
@Component
public class BatchPlanTableConRepositoryImpl extends BaseRepositoryImpl<BatchPlanTableCon, BatchPlanTableConDTO> implements BatchPlanTableConRepository {

    private final BatchPlanTableConMapper batchPlanTableConMapper;

    public BatchPlanTableConRepositoryImpl(BatchPlanTableConMapper batchPlanTableConMapper) {
        this.batchPlanTableConMapper = batchPlanTableConMapper;
    }

    @Override
    public List<BatchPlanTableConDO> selectJoinItem(BatchPlanTableConDO batchPlanFieldConDO) {
        return batchPlanTableConMapper.selectJoinItem(batchPlanFieldConDO);
    }

    @Override
    public int deleteByPlanBaseId(Long planBaseId) {
        return batchPlanTableConMapper.deleteByPlanBaseId(planBaseId);
    }
}
