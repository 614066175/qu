package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.BatchPlanRelTableDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanRelTableLineDTO;
import com.hand.hdsp.quality.api.dto.PlanWarningLevelDTO;
import com.hand.hdsp.quality.app.service.BatchPlanRelTableService;
import com.hand.hdsp.quality.domain.entity.BatchPlanRelTable;
import com.hand.hdsp.quality.domain.entity.BatchPlanRelTableLine;
import com.hand.hdsp.quality.domain.entity.PlanWarningLevel;
import com.hand.hdsp.quality.domain.repository.BatchPlanRelTableLineRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanRelTableRepository;
import com.hand.hdsp.quality.domain.repository.PlanWarningLevelRepository;
import com.hand.hdsp.quality.infra.constant.TableNameConstant;
import com.hand.hdsp.quality.infra.converter.PlanWarningLevelConverter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>批数据方案-表间规则表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Service
public class BatchPlanRelTableServiceImpl implements BatchPlanRelTableService {

    private final BatchPlanRelTableRepository batchPlanRelTableRepository;
    private final BatchPlanRelTableLineRepository batchPlanRelTableLineRepository;
    private final PlanWarningLevelRepository planWarningLevelRepository;
    private final PlanWarningLevelConverter planWarningLevelConverter;

    public BatchPlanRelTableServiceImpl(BatchPlanRelTableRepository batchPlanRelTableRepository, BatchPlanRelTableLineRepository batchPlanRelTableLineRepository, PlanWarningLevelRepository planWarningLevelRepository, PlanWarningLevelConverter planWarningLevelConverter) {
        this.batchPlanRelTableRepository = batchPlanRelTableRepository;
        this.batchPlanRelTableLineRepository = batchPlanRelTableLineRepository;
        this.planWarningLevelRepository = planWarningLevelRepository;
        this.planWarningLevelConverter = planWarningLevelConverter;
    }

    @Override
    public int delete(BatchPlanRelTableDTO batchPlanRelTableDTO) {
        List<BatchPlanRelTableLineDTO> batchPlanRelTableLineDTOList = batchPlanRelTableLineRepository.selectDTO(
                BatchPlanRelTableLine.FIELD_PLAN_REL_TABLE_ID, batchPlanRelTableDTO.getPlanRelTableId());
        if (batchPlanRelTableLineDTOList != null) {
            for (BatchPlanRelTableLineDTO batchPlanRelTableLineDTO : batchPlanRelTableLineDTOList) {
                batchPlanRelTableLineRepository.deleteDTO(batchPlanRelTableLineDTO);
            }
        }
        List<PlanWarningLevel> planWarningLevelList = planWarningLevelRepository.select(PlanWarningLevel.builder()
                .sourceId(batchPlanRelTableDTO.getPlanRelTableId())
                .sourceType(TableNameConstant.XQUA_BATCH_PLAN_REL_TABLE).build());
        if (planWarningLevelList != null) {
            for (PlanWarningLevel planWarningLevel :
                    planWarningLevelList) {
                planWarningLevelRepository.delete(planWarningLevel);
            }
        }
        return batchPlanRelTableRepository.deleteByPrimaryKey(batchPlanRelTableDTO);
    }

    @Override
    public BatchPlanRelTableDTO detail(Long planRelTableId) {
        BatchPlanRelTableDTO batchPlanRelTableDTO = batchPlanRelTableRepository.selectDTOByPrimaryKey(planRelTableId);
        List<BatchPlanRelTableLineDTO> batchPlanRelTableLineDTOList =
                batchPlanRelTableLineRepository.selectDTO(BatchPlanRelTable.FIELD_PLAN_REL_TABLE_ID, planRelTableId);
        List<PlanWarningLevel> planWarningLevelList = planWarningLevelRepository.select(
                PlanWarningLevel.builder()
                        .sourceId(batchPlanRelTableDTO.getPlanRelTableId())
                        .sourceType(TableNameConstant.XQUA_BATCH_PLAN_REL_TABLE).build());
        List<PlanWarningLevelDTO> planWarningLevelDTOList = new ArrayList<>();
        for (PlanWarningLevel planWarningLevel : planWarningLevelList) {
            planWarningLevelDTOList.add(planWarningLevelConverter.entityToDto(planWarningLevel));
        }
        batchPlanRelTableDTO.setBatchPlanRelTableLineDTOList(batchPlanRelTableLineDTOList);
        batchPlanRelTableDTO.setPlanWarningLevelDTOList(planWarningLevelDTOList);
        return batchPlanRelTableDTO;
    }

    @Override
    public void insert(BatchPlanRelTableDTO batchPlanRelTableDTO) {
        Long tenantId = batchPlanRelTableDTO.getTenantId();
        batchPlanRelTableRepository.insertDTOSelective(batchPlanRelTableDTO);
        if (batchPlanRelTableDTO.getBatchPlanRelTableLineDTOList() != null) {

            for (BatchPlanRelTableLineDTO batchPlanRelTableLineDTO : batchPlanRelTableDTO.getBatchPlanRelTableLineDTOList()) {
                batchPlanRelTableLineDTO.setPlanRelTableId(batchPlanRelTableDTO.getPlanRelTableId());
                batchPlanRelTableLineDTO.setTenantId(tenantId);
                batchPlanRelTableLineRepository.insertDTOSelective(batchPlanRelTableLineDTO);
            }
        }
        if (batchPlanRelTableDTO.getPlanWarningLevelDTOList() != null) {
            for (PlanWarningLevelDTO planWarningLevelDTO : batchPlanRelTableDTO.getPlanWarningLevelDTOList()) {
                planWarningLevelDTO.setSourceId(batchPlanRelTableDTO.getPlanRelTableId());
                planWarningLevelDTO.setSourceType(TableNameConstant.XQUA_BATCH_PLAN_REL_TABLE);
                planWarningLevelDTO.setTenantId(tenantId);
                planWarningLevelRepository.insertDTOSelective(planWarningLevelDTO);
            }
        }
    }

    @Override
    public void update(BatchPlanRelTableDTO batchPlanRelTableDTO) {
        Long tenantId = batchPlanRelTableDTO.getTenantId();
        batchPlanRelTableRepository.updateDTOWhereTenant(batchPlanRelTableDTO, tenantId);
        if (batchPlanRelTableDTO.getBatchPlanRelTableLineDTOList() != null) {

            for (BatchPlanRelTableLineDTO batchPlanRelTableLineDTO : batchPlanRelTableDTO.getBatchPlanRelTableLineDTOList()) {
                batchPlanRelTableLineDTO.setPlanRelTableId(batchPlanRelTableDTO.getPlanRelTableId());
                batchPlanRelTableLineDTO.setTenantId(tenantId);
                if (batchPlanRelTableLineRepository.selectOne(BatchPlanRelTableLine.builder()
                        .lineId(batchPlanRelTableLineDTO.getLineId())
                        .planRelTableId(batchPlanRelTableLineDTO.getPlanRelTableId()).build()) != null) {
                    batchPlanRelTableLineRepository.updateDTOWhereTenant(batchPlanRelTableLineDTO, tenantId);
                } else {
                    batchPlanRelTableLineRepository.insertDTOSelective(batchPlanRelTableLineDTO);
                }
            }
        }
        if (batchPlanRelTableDTO.getPlanWarningLevelDTOList() != null) {
            for (PlanWarningLevelDTO planWarningLevelDTO : batchPlanRelTableDTO.getPlanWarningLevelDTOList()) {
                planWarningLevelDTO.setSourceId(batchPlanRelTableDTO.getPlanRelTableId());
                planWarningLevelDTO.setSourceType(TableNameConstant.XQUA_BATCH_PLAN_REL_TABLE);
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
