package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.BatchPlanFieldDTO;
import com.hand.hdsp.quality.app.service.BatchPlanFieldService;
import com.hand.hdsp.quality.domain.entity.BatchPlanFieldLine;
import com.hand.hdsp.quality.domain.repository.BatchPlanFieldLineRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanFieldRepository;
import org.springframework.stereotype.Service;

/**
 * <p>批数据方案-字段规则表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Service
public class BatchPlanFieldServiceImpl implements BatchPlanFieldService {

    private final BatchPlanFieldRepository batchPlanFieldRepository;
    private final BatchPlanFieldLineRepository batchPlanFieldLineRepository;

    public BatchPlanFieldServiceImpl(BatchPlanFieldRepository batchPlanFieldRepository, BatchPlanFieldLineRepository batchPlanFieldLineRepository) {
        this.batchPlanFieldRepository = batchPlanFieldRepository;
        this.batchPlanFieldLineRepository = batchPlanFieldLineRepository;
    }

    @Override
    public int delete(BatchPlanFieldDTO batchPlanFieldDTO) {
        batchPlanFieldLineRepository.batchDTODelete(
                batchPlanFieldLineRepository.selectDTO
                        (BatchPlanFieldLine.FIELD_PLAN_FIELD_ID, batchPlanFieldDTO.getPlanFieldId()));
        return batchPlanFieldRepository.deleteByPrimaryKey(batchPlanFieldDTO);
    }
}
