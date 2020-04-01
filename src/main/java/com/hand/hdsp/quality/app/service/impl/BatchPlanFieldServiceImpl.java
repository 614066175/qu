package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.BatchPlanFieldDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanFieldLineDTO;
import com.hand.hdsp.quality.app.service.BatchPlanFieldService;
import com.hand.hdsp.quality.domain.entity.BatchPlanFieldLine;
import com.hand.hdsp.quality.domain.repository.BatchPlanFieldLineRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanFieldRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @Transactional(rollbackFor = Exception.class)
    public int delete(BatchPlanFieldDTO batchPlanFieldDTO) {
        List<BatchPlanFieldLineDTO> batchPlanFieldLineDTOList =
                batchPlanFieldLineRepository.selectDTO(BatchPlanFieldLine.FIELD_PLAN_FIELD_ID,
                        batchPlanFieldDTO.getPlanFieldId());
        if (batchPlanFieldLineDTOList != null) {
            for (BatchPlanFieldLineDTO batchPlanFieldLineDTO :
                    batchPlanFieldLineDTOList) {
                batchPlanFieldLineRepository.deleteDTO(batchPlanFieldLineDTO);
            }
        }
        return batchPlanFieldRepository.deleteByPrimaryKey(batchPlanFieldDTO);
    }
}
