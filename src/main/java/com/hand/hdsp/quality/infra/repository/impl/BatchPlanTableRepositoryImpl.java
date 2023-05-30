package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.BatchPlanTableDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanTable;
import com.hand.hdsp.quality.domain.repository.BatchPlanTableRepository;
import com.hand.hdsp.quality.infra.dataobject.BatchPlanTableDO;
import com.hand.hdsp.quality.infra.mapper.BatchPlanTableMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>批数据方案-表级规则表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Component
public class BatchPlanTableRepositoryImpl extends BaseRepositoryImpl<BatchPlanTable, BatchPlanTableDTO> implements BatchPlanTableRepository {

    private final BatchPlanTableMapper batchPlanTableMapper;

    public BatchPlanTableRepositoryImpl(BatchPlanTableMapper batchPlanTableMapper) {
        this.batchPlanTableMapper = batchPlanTableMapper;
    }

    @Override
    public List<BatchPlanTableDO> list(BatchPlanTableDO batchPlanTableDO) {
        return batchPlanTableMapper.list(batchPlanTableDO);
    }

    @Override
    public int deleteByPlanBaseId(Long planBaseId) {
        return batchPlanTableMapper.deleteByPlanBaseId(planBaseId);
    }

    @Override
    public List<BatchPlanTableDTO> selectDetailList(BatchPlanTableDTO batchPlanTableDTO) {
        return batchPlanTableMapper.selectDetailList(batchPlanTableDTO);
    }
    @Override
    public List<BatchPlanTableDTO> selectTableList(BatchPlanTableDTO batchPlanTableDTO) {
        return batchPlanTableMapper.getPlanTable(batchPlanTableDTO);
    }


}
