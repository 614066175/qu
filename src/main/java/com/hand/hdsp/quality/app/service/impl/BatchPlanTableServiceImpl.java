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
import com.hand.hdsp.quality.infra.converter.PlanWarningLevelConverter;
import com.hand.hdsp.quality.infra.dataobject.BatchPlanTableDO;
import io.choerodon.mybatis.domain.AuditDomain;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final PlanWarningLevelConverter planWarningLevelConverter;

    public BatchPlanTableServiceImpl(BatchPlanTableRepository batchPlanTableRepository,
                                     BatchPlanTableLineRepository batchPlanTableLineRepository,
                                     PlanWarningLevelRepository planWarningLevelRepository, PlanWarningLevelConverter planWarningLevelConverter) {
        this.batchPlanTableRepository = batchPlanTableRepository;
        this.batchPlanTableLineRepository = batchPlanTableLineRepository;
        this.planWarningLevelRepository = planWarningLevelRepository;
        this.planWarningLevelConverter = planWarningLevelConverter;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(BatchPlanTableDTO batchPlanTableDTO) {
        List<BatchPlanTableLineDTO> batchPlanTableLineDTOList =
                batchPlanTableLineRepository.selectDTO(
                        BatchPlanTableLine.FIELD_PLAN_TABLE_ID, batchPlanTableDTO.getPlanTableId());
        if (batchPlanTableLineDTOList != null) {
            for (BatchPlanTableLineDTO batchPlanTableLineDTO : batchPlanTableLineDTOList) {
                planWarningLevelRepository.deleteByParentId(batchPlanTableLineDTO.getPlanTableLineId(),
                        TableNameConstant.XQUA_BATCH_PLAN_TABLE_LINE);
            }
            batchPlanTableLineRepository.deleteByParentId(batchPlanTableDTO.getPlanTableId());
        }
        return batchPlanTableRepository.deleteByPrimaryKey(batchPlanTableDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
    @Transactional(rollbackFor = Exception.class)
    public void update(BatchPlanTableDTO batchPlanTableDTO) {
        Long tenantId = batchPlanTableDTO.getTenantId();
        batchPlanTableRepository.updateDTOWhereTenant(batchPlanTableDTO, tenantId);
        if (batchPlanTableDTO.getBatchPlanTableLineDTOList() != null) {
            for (BatchPlanTableLineDTO batchPlanTableLineDTO : batchPlanTableDTO.getBatchPlanTableLineDTOList()) {
                if (AuditDomain.RecordStatus.update.equals(batchPlanTableLineDTO.get_status())) {
                    batchPlanTableLineRepository.updateDTOWhereTenant(batchPlanTableLineDTO, tenantId);
                } else if (AuditDomain.RecordStatus.create.equals(batchPlanTableLineDTO.get_status())) {
                    batchPlanTableLineDTO.setPlanTableId(batchPlanTableDTO.getPlanTableId());
                    batchPlanTableLineDTO.setTenantId(tenantId);
                    batchPlanTableLineRepository.insertDTOSelective(batchPlanTableLineDTO);
                } else if (AuditDomain.RecordStatus.delete.equals(batchPlanTableLineDTO.get_status())) {
                    batchPlanTableLineRepository.deleteByPrimaryKey(batchPlanTableLineDTO);
                }
                if (batchPlanTableLineDTO.getPlanWarningLevelDTOList() != null) {
                    for (PlanWarningLevelDTO planWarningLevelDTO : batchPlanTableLineDTO.getPlanWarningLevelDTOList()) {
                        if (AuditDomain.RecordStatus.update.equals(planWarningLevelDTO.get_status())) {
                            planWarningLevelRepository.updateDTOWhereTenant(planWarningLevelDTO, tenantId);
                        } else if (AuditDomain.RecordStatus.create.equals(planWarningLevelDTO.get_status())) {
                            planWarningLevelDTO.setSourceId(batchPlanTableLineDTO.getPlanTableLineId());
                            planWarningLevelDTO.setSourceType(TableNameConstant.XQUA_BATCH_PLAN_TABLE_LINE);
                            planWarningLevelDTO.setTenantId(tenantId);
                            planWarningLevelRepository.insertDTOSelective(planWarningLevelDTO);
                        } else if (AuditDomain.RecordStatus.delete.equals(planWarningLevelDTO.get_status())) {
                            planWarningLevelRepository.deleteByPrimaryKey(planWarningLevelDTO);
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
            List<PlanWarningLevel> planWarningLevelList = planWarningLevelRepository.select(
                    PlanWarningLevel.builder()
                            .sourceId(batchPlanTableLineDTO.getPlanTableLineId())
                            .sourceType(TableNameConstant.XQUA_BATCH_PLAN_TABLE_LINE).build());
            List<PlanWarningLevelDTO> planWarningLevelDTOList = new ArrayList<>();
            for (PlanWarningLevel planWarningLevel : planWarningLevelList) {
                planWarningLevelDTOList.add(planWarningLevelConverter.entityToDto(planWarningLevel));
            }
            batchPlanTableLineDTO.setPlanWarningLevelDTOList(planWarningLevelDTOList);
        }
        batchPlanTableDTO.setBatchPlanTableLineDTOList(batchPlanTableLineDTOList);
        return batchPlanTableDTO;
    }
}
