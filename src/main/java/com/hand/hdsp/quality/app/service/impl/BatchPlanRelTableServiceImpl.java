package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.BatchPlanRelTableDTO;
import com.hand.hdsp.quality.app.service.BatchPlanRelTableService;
import com.hand.hdsp.quality.domain.entity.BatchPlanRelTableLine;
import com.hand.hdsp.quality.domain.repository.BatchPlanRelTableLineRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanRelTableRepository;
import org.springframework.stereotype.Service;

/**
 * <p>批数据方案-表间规则表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Service
public class BatchPlanRelTableServiceImpl implements BatchPlanRelTableService {

    private final BatchPlanRelTableRepository batchPlanRelTableRepository;
    private final BatchPlanRelTableLineRepository batchPlanRelTableLineRepository;

    public BatchPlanRelTableServiceImpl(BatchPlanRelTableRepository batchPlanRelTableRepository, BatchPlanRelTableLineRepository batchPlanRelTableLineRepository) {
        this.batchPlanRelTableRepository = batchPlanRelTableRepository;
        this.batchPlanRelTableLineRepository = batchPlanRelTableLineRepository;
    }

    @Override
    public int delete(BatchPlanRelTableDTO batchPlanRelTableDTO) {
        batchPlanRelTableLineRepository.batchDTODelete(
                batchPlanRelTableLineRepository.selectDTO
                        (BatchPlanRelTableLine.FIELD_PLAN_REL_TABLE_ID, batchPlanRelTableDTO.getPlanRelTableId()));
        return batchPlanRelTableRepository.deleteByPrimaryKey(batchPlanRelTableDTO);
    }
}
