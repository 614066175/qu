package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.BatchPlanBaseService;
import com.hand.hdsp.quality.domain.entity.*;
import com.hand.hdsp.quality.domain.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

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
        List<BatchPlanTableDTO> batchPlanTableDTOList = batchPlanTableRepository.
                selectDTO(BatchPlanTable.FIELD_PLAN_BASE_ID, batchPlanBaseDTO.getPlanBaseId());
        List<BatchPlanFieldDTO> batchPlanFieldDTOList = batchPlanFieldRepository.
                selectDTO(BatchPlanField.FIELD_PLAN_BASE_ID, batchPlanBaseDTO.getPlanBaseId());
        List<BatchPlanRelTableDTO> batchPlanRelTableDTOList = batchPlanRelTableRepository.
                selectDTO(BatchPlanRelTable.FIELD_PLAN_BASE_ID, batchPlanBaseDTO.getPlanBaseId());
        for (BatchPlanTableDTO batchPlanTableDTO : batchPlanTableDTOList) {
            batchPlanTableLineRepository.batchDTODelete(
                    batchPlanTableLineRepository.selectDTO
                            (BatchPlanTableLine.FIELD_PLAN_TABLE_ID, batchPlanTableDTO.getPlanTableId()));
        }
        for (BatchPlanFieldDTO batchPlanFieldDTO : batchPlanFieldDTOList) {
            batchPlanFieldLineRepository.batchDTODelete(
                    batchPlanFieldLineRepository.selectDTO
                            (BatchPlanFieldLine.FIELD_PLAN_FIELD_ID, batchPlanFieldDTO.getPlanFieldId()));
        }
        for (BatchPlanRelTableDTO batchPlanRelTableDTO : batchPlanRelTableDTOList) {
            batchPlanRelTableLineRepository.batchDTODelete(
                    batchPlanRelTableLineRepository.selectDTO
                            (BatchPlanRelTableLine.FIELD_PLAN_REL_TABLE_ID, batchPlanRelTableDTO.getPlanRelTableId()));
        }
        batchPlanTableRepository.batchDTODeleteByPrimaryKey(batchPlanTableDTOList);
        batchPlanFieldRepository.batchDTODeleteByPrimaryKey(batchPlanFieldDTOList);
        batchPlanRelTableRepository.batchDTODeleteByPrimaryKey(batchPlanRelTableDTOList);
        return batchPlanBaseRepository.deleteByPrimaryKey(batchPlanBaseDTO);
    }

    @Override
    public void insert(BatchPlanBaseDTO batchPlanBaseDTO) {
        Long tenantId = batchPlanBaseDTO.getTenantId();
        batchPlanBaseRepository.insertDTOSelective(batchPlanBaseDTO);
        if (batchPlanBaseDTO.getBatchPlanTableDTO() != null) {
            BatchPlanTableDTO batchPlanTableDTO = batchPlanBaseDTO.getBatchPlanTableDTO();
            batchPlanTableDTO.setPlanBaseId(batchPlanBaseDTO.getPlanBaseId());
            batchPlanTableDTO.setTenantId(tenantId);
            batchPlanTableRepository.insertDTOSelective(batchPlanTableDTO);
            if (batchPlanTableDTO.getBatchPlanTableLineDTOList() != null) {
                for (BatchPlanTableLineDTO batchPlanTableLineDTO : batchPlanTableDTO.getBatchPlanTableLineDTOList()) {
                    batchPlanTableLineDTO.setPlanTableId(batchPlanTableDTO.getPlanTableId());
                    batchPlanTableLineDTO.setTenantId(tenantId);
                    batchPlanTableLineRepository.insertDTOSelective(batchPlanTableLineDTO);
                }
            }
        }
        if (batchPlanBaseDTO.getBatchPlanFieldDTO() != null) {
            BatchPlanFieldDTO batchPlanFieldDTO = batchPlanBaseDTO.getBatchPlanFieldDTO();
            batchPlanFieldDTO.setPlanBaseId(batchPlanBaseDTO.getPlanBaseId());
            batchPlanFieldDTO.setTenantId(tenantId);
            batchPlanFieldRepository.insertDTOSelective(batchPlanFieldDTO);
            if (batchPlanFieldDTO.getBatchPlanFieldLineDTOList() != null) {
                for (BatchPlanFieldLineDTO batchPlanFieldLineDTO : batchPlanFieldDTO.getBatchPlanFieldLineDTOList()) {
                    batchPlanFieldLineDTO.setPlanFieldId(batchPlanFieldDTO.getPlanFieldId());
                    batchPlanFieldLineDTO.setTenantId(tenantId);
                    batchPlanFieldLineRepository.insertDTOSelective(batchPlanFieldLineDTO);
                }
            }
        }
        if (batchPlanBaseDTO.getBatchPlanRelTableDTO() != null) {
            BatchPlanRelTableDTO batchPlanRelTableDTO = batchPlanBaseDTO.getBatchPlanRelTableDTO();
            batchPlanRelTableDTO.setPlanBaseId(batchPlanBaseDTO.getPlanBaseId());
            batchPlanRelTableDTO.setTenantId(tenantId);
            batchPlanRelTableRepository.insertDTOSelective(batchPlanRelTableDTO);
            if (batchPlanRelTableDTO.getBatchPlanRelTableLineDTOList() != null) {
                for (BatchPlanRelTableLineDTO batchPlanRelTableLineDTO : batchPlanRelTableDTO.getBatchPlanRelTableLineDTOList()) {
                    batchPlanRelTableLineDTO.setPlanRelTableId(batchPlanRelTableDTO.getPlanRelTableId());
                    batchPlanRelTableLineDTO.setTenantId(tenantId);
                    batchPlanRelTableLineRepository.insertDTOSelective(batchPlanRelTableLineDTO);
                }
            }
        }
    }
}