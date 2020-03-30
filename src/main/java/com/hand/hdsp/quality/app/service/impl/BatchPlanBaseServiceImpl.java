package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.BatchPlanBaseService;
import com.hand.hdsp.quality.domain.repository.*;
import org.springframework.stereotype.Service;

/**
 * <p>批数据方案-基础配置表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Service
public class BatchPlanBaseServiceImpl implements BatchPlanBaseService {

    private final BatchPlanBaseRepository batchPlanBaseRepository;
    private final BatchPlanTableRepository batchPlanTableRepository;
    private final BatchPlanTableLineRepository batchPlanTableLineRepository;
    private final BatchPlanFieldRepository batchPlanFieldRepository;
    private final BatchPlanFieldLineRepository batchPlanFieldLineRepository;
    private final BatchPlanRelTableRepository batchPlanRelTableRepository;
    private final BatchPlanRelTableLineRepository batchPlanRelTableLineRepository;

    public BatchPlanBaseServiceImpl(BatchPlanBaseRepository batchPlanBaseRepository,
                                    BatchPlanTableRepository batchPlanTableRepository,
                                    BatchPlanTableLineRepository batchPlanTableLineRepository,
                                    BatchPlanFieldRepository batchPlanFieldRepository, BatchPlanFieldLineRepository batchPlanFieldLineRepository,
                                    BatchPlanRelTableRepository batchPlanRelTableRepository, BatchPlanRelTableLineRepository batchPlanRelTableLineRepository) {
        this.batchPlanBaseRepository = batchPlanBaseRepository;
        this.batchPlanTableRepository = batchPlanTableRepository;
        this.batchPlanTableLineRepository = batchPlanTableLineRepository;
        this.batchPlanFieldRepository = batchPlanFieldRepository;
        this.batchPlanFieldLineRepository = batchPlanFieldLineRepository;
        this.batchPlanRelTableRepository = batchPlanRelTableRepository;
        this.batchPlanRelTableLineRepository = batchPlanRelTableLineRepository;
    }

    @Override
    public int delete(BatchPlanBaseDTO batchPlanBaseDTO) {
        if (batchPlanBaseDTO.getBatchPlanTableDTOList() != null) {
            for (BatchPlanTableDTO batchPlanTableDTO : batchPlanBaseDTO.getBatchPlanTableDTOList()) {
                if (batchPlanTableDTO.getBatchPlanTableLineDTOList() != null) {
                    for (BatchPlanTableLineDTO batchPlanTableLineDTO :
                            batchPlanTableDTO.getBatchPlanTableLineDTOList()) {
                        batchPlanTableLineRepository.deleteDTO(batchPlanTableLineDTO);
                    }
                }
                batchPlanTableRepository.deleteDTO(batchPlanTableDTO);
            }
        }

        if (batchPlanBaseDTO.getBatchPlanFieldDTOList() != null) {
            for (BatchPlanFieldDTO batchPlanFieldDTO : batchPlanBaseDTO.getBatchPlanFieldDTOList()) {
                if (batchPlanFieldDTO.getBatchPlanFieldLineDTOList() != null) {
                    for (BatchPlanFieldLineDTO batchPlanFieldLineDTO :
                            batchPlanFieldDTO.getBatchPlanFieldLineDTOList()) {
                        batchPlanFieldLineRepository.deleteDTO(batchPlanFieldLineDTO);
                    }
                }
                batchPlanFieldRepository.deleteDTO(batchPlanFieldDTO);
            }
        }

        if (batchPlanBaseDTO.getBatchPlanRelTableDTOList() != null) {
            for (BatchPlanRelTableDTO batchPlanRelTableDTO : batchPlanBaseDTO.getBatchPlanRelTableDTOList()) {
                if (batchPlanRelTableDTO.getBatchPlanRelTableLineDTOList() != null) {
                    for (BatchPlanRelTableLineDTO batchPlanRelTableLineDTO :
                            batchPlanRelTableDTO.getBatchPlanRelTableLineDTOList()) {
                        batchPlanRelTableLineRepository.deleteDTO(batchPlanRelTableLineDTO);
                    }
                }
                batchPlanRelTableRepository.deleteDTO(batchPlanRelTableDTO);
            }
        }
        return batchPlanBaseRepository.deleteByPrimaryKey(batchPlanBaseDTO);
    }
}