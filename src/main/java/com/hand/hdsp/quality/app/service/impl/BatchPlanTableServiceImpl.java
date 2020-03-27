package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.BatchPlanTableDTO;
import com.hand.hdsp.quality.app.service.BatchPlanTableService;
import com.hand.hdsp.quality.domain.entity.BatchPlanTableLine;
import com.hand.hdsp.quality.domain.repository.BatchPlanTableLineRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanTableRepository;
import org.springframework.stereotype.Service;

/**
 * <p>批数据方案-表级规则表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Service
public class BatchPlanTableServiceImpl implements BatchPlanTableService {

    private final BatchPlanTableRepository batchPlanTableRepository;
    private final BatchPlanTableLineRepository batchPlanTableLineRepository;

    public BatchPlanTableServiceImpl(BatchPlanTableRepository batchPlanTableRepository, BatchPlanTableLineRepository batchPlanTableLineRepository) {
        this.batchPlanTableRepository = batchPlanTableRepository;
        this.batchPlanTableLineRepository = batchPlanTableLineRepository;
    }

    @Override
    public int delete(BatchPlanTableDTO batchPlanTableDTO) {
        batchPlanTableLineRepository.batchDTODelete(
                batchPlanTableLineRepository.selectDTO
                        (BatchPlanTableLine.FIELD_PLAN_TABLE_ID, batchPlanTableDTO.getPlanTableId()));
        return batchPlanTableRepository.deleteByPrimaryKey(batchPlanTableDTO);
    }
}
