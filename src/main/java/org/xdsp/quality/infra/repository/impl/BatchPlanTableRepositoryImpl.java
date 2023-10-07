package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.BatchPlanTableDTO;
import org.xdsp.quality.domain.entity.BatchPlanTable;
import org.xdsp.quality.domain.repository.BatchPlanTableRepository;
import org.xdsp.quality.infra.dataobject.BatchPlanTableDO;
import org.xdsp.quality.infra.mapper.BatchPlanTableMapper;

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
        return batchPlanTableMapper.selectPlanTable(batchPlanTableDTO);
    }


}
