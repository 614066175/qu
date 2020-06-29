package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchPlanTableConDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanTableCon;
import com.hand.hdsp.quality.domain.repository.BatchPlanTableConRepository;
import com.hand.hdsp.quality.infra.dataobject.BatchPlanTableConDO;
import com.hand.hdsp.quality.infra.mapper.BatchPlanTableConMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
