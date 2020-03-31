package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.BatchPlanTableDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanTableLineDTO;
import com.hand.hdsp.quality.api.dto.PlanWarningLevelDTO;
import com.hand.hdsp.quality.app.service.BatchPlanTableService;
import com.hand.hdsp.quality.domain.entity.BatchPlanTableLine;
import com.hand.hdsp.quality.domain.entity.PlanWarningLevel;
import com.hand.hdsp.quality.domain.repository.BatchPlanTableLineRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanTableRepository;
import com.hand.hdsp.quality.domain.repository.PlanWarningLevelRepository;
import com.hand.hdsp.quality.infra.constant.TableNameConstant;
import com.hand.hdsp.quality.infra.dataobject.BatchPlanTableDO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>批数据方案-表级规则表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Service
public class BatchPlanTableServiceImpl implements BatchPlanTableService {

    private final BatchPlanTableRepository batchPlanTableRepository;
    private final BatchPlanTableLineRepository batchPlanTableLineRepository;
    private final PlanWarningLevelRepository planWarningLevelRepository;

    public BatchPlanTableServiceImpl(BatchPlanTableRepository batchPlanTableRepository,
                                     BatchPlanTableLineRepository batchPlanTableLineRepository,
                                     PlanWarningLevelRepository planWarningLevelRepository) {
        this.batchPlanTableRepository = batchPlanTableRepository;
        this.batchPlanTableLineRepository = batchPlanTableLineRepository;
        this.planWarningLevelRepository = planWarningLevelRepository;
    }

    @Override
    public int delete(BatchPlanTableDTO batchPlanTableDTO) {
        if (batchPlanTableDTO.getBatchPlanTableLineDTOList() != null) {
            for (BatchPlanTableLineDTO batchPlanTableLineDTO : batchPlanTableDTO.getBatchPlanTableLineDTOList()) {
                batchPlanTableLineRepository.deleteDTO(batchPlanTableLineDTO);
                if (batchPlanTableLineDTO.getPlanWarningLevelDTOList() != null) {
                    for (PlanWarningLevelDTO planWarningLevelDTO : batchPlanTableLineDTO.getPlanWarningLevelDTOList()) {
                        planWarningLevelRepository.deleteDTO(planWarningLevelDTO);
                    }
                }
            }
        }
        return batchPlanTableRepository.deleteByPrimaryKey(batchPlanTableDTO);
    }

    @Override
    public void insert(BatchPlanTableDTO batchPlanTableDTO) {
        Long tenantId = batchPlanTableDTO.getTenantId();
        batchPlanTableRepository.insertDTOSelective(batchPlanTableDTO);
        if (batchPlanTableDTO.getBatchPlanTableLineDTOList() != null) {

            for (BatchPlanTableLineDTO batchPlanTableLineDTO : batchPlanTableDTO.getBatchPlanTableLineDTOList()) {
                batchPlanTableLineDTO.setPlanTableId(batchPlanTableDTO.getPlanTableId());
                batchPlanTableLineDTO.setTenantId(tenantId);
                batchPlanTableLineRepository.insertDTOSelective(batchPlanTableLineDTO);

                if (batchPlanTableLineDTO.getPlanWarningLevelDTOList() != null) {
                    for (PlanWarningLevelDTO planWarningLevelDTO : batchPlanTableLineDTO.getPlanWarningLevelDTOList()) {
                        planWarningLevelDTO.setSourceId(batchPlanTableLineDTO.getPlanTableLineId());
                        planWarningLevelDTO.setSourceType(TableNameConstant.XQUA_BATCH_PLAN_TABLE_LINE);
                        planWarningLevelDTO.setTenantId(tenantId);
                        planWarningLevelRepository.insertDTOSelective(planWarningLevelDTO);
                    }
                }
            }
        }
    }

    @Override
    public void update(BatchPlanTableDTO batchPlanTableDTO) {
        Long tenantId = batchPlanTableDTO.getTenantId();
        batchPlanTableRepository.updateDTOWhereTenant(batchPlanTableDTO, tenantId);
        if (batchPlanTableDTO.getBatchPlanTableLineDTOList() != null) {

            for (BatchPlanTableLineDTO batchPlanTableLineDTO : batchPlanTableDTO.getBatchPlanTableLineDTOList()) {
                batchPlanTableLineDTO.setPlanTableId(batchPlanTableDTO.getPlanTableId());
                batchPlanTableLineDTO.setTenantId(tenantId);
                if (batchPlanTableLineRepository.selectOne(BatchPlanTableLine.builder()
                        .planTableLineId(batchPlanTableLineDTO.getPlanTableLineId())
                        .planTableId(batchPlanTableLineDTO.getPlanTableId()).build()) != null) {
                    batchPlanTableLineRepository.updateDTOWhereTenant(batchPlanTableLineDTO, tenantId);
                } else {
                    batchPlanTableLineRepository.insertDTOSelective(batchPlanTableLineDTO);
                }

                if (batchPlanTableLineDTO.getPlanWarningLevelDTOList() != null) {
                    for (PlanWarningLevelDTO planWarningLevelDTO : batchPlanTableLineDTO.getPlanWarningLevelDTOList()) {
                        planWarningLevelDTO.setSourceId(batchPlanTableLineDTO.getPlanTableLineId());
                        planWarningLevelDTO.setSourceType(TableNameConstant.XQUA_BATCH_PLAN_TABLE_LINE);
                        planWarningLevelDTO.setTenantId(tenantId);
                        if (planWarningLevelRepository.selectOne(PlanWarningLevel.builder()
                                .sourceId(planWarningLevelDTO.getSourceId())
                                .sourceType(planWarningLevelDTO.getSourceType()).build()) != null) {
                            planWarningLevelRepository.updateDTOWhereTenant(planWarningLevelDTO, tenantId);
                        } else {
                            planWarningLevelRepository.insertDTOSelective(planWarningLevelDTO);
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<BatchPlanTableDO> list(BatchPlanTableDO batchPlanTableDO) {
        return batchPlanTableRepository.list(batchPlanTableDO);
    }

    @Override
    public BatchPlanTableDTO detail(Long planTableId) {
        BatchPlanTableDTO batchPlanTableDTO = batchPlanTableRepository.selectDTOByPrimaryKey(planTableId);
        List<BatchPlanTableLineDTO> batchPlanTableLineDTOList =
                batchPlanTableLineRepository.selectDTO(BatchPlanTableLine.FIELD_PLAN_TABLE_ID, planTableId);
        for (BatchPlanTableLineDTO batchPlanTableLineDTO : batchPlanTableLineDTOList) {
            batchPlanTableLineDTO.setPlanWarningLevelDTOList(
                    planWarningLevelRepository.selectDTO(
                            PlanWarningLevel.FIELD_SOURCE_ID, batchPlanTableLineDTO.getPlanTableLineId()));
        }
        batchPlanTableDTO.setBatchPlanTableLineDTOList(batchPlanTableLineDTOList);
        return batchPlanTableDTO;
    }
}
