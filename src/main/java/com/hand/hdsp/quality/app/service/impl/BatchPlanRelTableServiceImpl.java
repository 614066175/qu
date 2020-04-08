package com.hand.hdsp.quality.app.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.BatchPlanRelTableDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanRelTableLineDTO;
import com.hand.hdsp.quality.api.dto.DatasourceDTO;
import com.hand.hdsp.quality.api.dto.PlanWarningLevelDTO;
import com.hand.hdsp.quality.app.service.BatchPlanRelTableService;
import com.hand.hdsp.quality.domain.entity.BatchPlanRelTable;
import com.hand.hdsp.quality.domain.entity.PlanWarningLevel;
import com.hand.hdsp.quality.domain.repository.BatchPlanRelTableLineRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanRelTableRepository;
import com.hand.hdsp.quality.domain.repository.PlanWarningLevelRepository;
import com.hand.hdsp.quality.infra.constant.TableNameConstant;
import com.hand.hdsp.quality.infra.converter.PlanWarningLevelConverter;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.core.util.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final DatasourceFeign datasourceFeign;

    public BatchPlanRelTableServiceImpl(BatchPlanRelTableRepository batchPlanRelTableRepository,
                                        BatchPlanRelTableLineRepository batchPlanRelTableLineRepository,
                                        PlanWarningLevelRepository planWarningLevelRepository,
                                        PlanWarningLevelConverter planWarningLevelConverter,
                                        DatasourceFeign datasourceFeign) {
        this.batchPlanRelTableRepository = batchPlanRelTableRepository;
        this.batchPlanRelTableLineRepository = batchPlanRelTableLineRepository;
        this.planWarningLevelRepository = planWarningLevelRepository;
        this.planWarningLevelConverter = planWarningLevelConverter;
        this.datasourceFeign = datasourceFeign;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(BatchPlanRelTableDTO batchPlanRelTableDTO) {
        batchPlanRelTableLineRepository.deleteByParentId(batchPlanRelTableDTO.getPlanRelTableId());
        planWarningLevelRepository.deleteByParentId(batchPlanRelTableDTO.getPlanRelTableId(),
                TableNameConstant.XQUA_BATCH_PLAN_REL_TABLE);
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
    @Transactional(rollbackFor = Exception.class)
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
    @Transactional(rollbackFor = Exception.class)
    public void update(BatchPlanRelTableDTO batchPlanRelTableDTO) {
        Long tenantId = batchPlanRelTableDTO.getTenantId();
        batchPlanRelTableRepository.updateDTOWhereTenant(batchPlanRelTableDTO, tenantId);
        if (batchPlanRelTableDTO.getBatchPlanRelTableLineDTOList() != null) {
            for (BatchPlanRelTableLineDTO batchPlanRelTableLineDTO : batchPlanRelTableDTO.getBatchPlanRelTableLineDTOList()) {
                if (AuditDomain.RecordStatus.update.equals(batchPlanRelTableLineDTO.get_status())) {
                    batchPlanRelTableLineRepository.updateDTOWhereTenant(batchPlanRelTableLineDTO, tenantId);
                } else if (AuditDomain.RecordStatus.create.equals(batchPlanRelTableLineDTO.get_status())) {
                    batchPlanRelTableLineDTO.setPlanRelTableId(batchPlanRelTableDTO.getPlanRelTableId());
                    batchPlanRelTableLineDTO.setTenantId(tenantId);
                    batchPlanRelTableLineRepository.insertDTOSelective(batchPlanRelTableLineDTO);
                } else if (AuditDomain.RecordStatus.delete.equals(batchPlanRelTableLineDTO.get_status())) {
                    batchPlanRelTableLineRepository.deleteByPrimaryKey(batchPlanRelTableLineDTO);
                }
            }
        }
        if (batchPlanRelTableDTO.getPlanWarningLevelDTOList() != null) {
            for (PlanWarningLevelDTO planWarningLevelDTO : batchPlanRelTableDTO.getPlanWarningLevelDTOList()) {
                if (AuditDomain.RecordStatus.update.equals(planWarningLevelDTO.get_status())) {
                    planWarningLevelRepository.updateDTOWhereTenant(planWarningLevelDTO, tenantId);
                } else if (AuditDomain.RecordStatus.create.equals(planWarningLevelDTO.get_status())) {
                    planWarningLevelDTO.setSourceId(batchPlanRelTableDTO.getPlanRelTableId());
                    planWarningLevelDTO.setSourceType(TableNameConstant.XQUA_BATCH_PLAN_REL_TABLE);
                    planWarningLevelDTO.setTenantId(tenantId);
                    planWarningLevelRepository.insertDTOSelective(planWarningLevelDTO);
                } else if (AuditDomain.RecordStatus.delete.equals(planWarningLevelDTO.get_status())) {
                    planWarningLevelRepository.deleteByPrimaryKey(planWarningLevelDTO);
                }
            }
        }
    }

    @Override
    public Page<BatchPlanRelTableDTO> list(PageRequest pageRequest, BatchPlanRelTableDTO batchPlanRelTableDTO) {
        Page<BatchPlanRelTableDTO> list = batchPlanRelTableRepository.pageAndSortDTO(pageRequest, batchPlanRelTableDTO);
        for (BatchPlanRelTableDTO batchPlanRelTableDTO1 : list) {
            ResponseEntity<String> result = datasourceFeign.detail(batchPlanRelTableDTO.getTenantId(), batchPlanRelTableDTO1.getRelDatasourceId());
            DatasourceDTO datasourceDTO = ResponseUtils.getResponse(result, new TypeReference<DatasourceDTO>() {
            }, (httpStatus, response) -> {
                throw new CommonException(response);
            }, exceptionResponse -> {
                throw new CommonException(exceptionResponse.getMessage());
            });
            if (datasourceDTO != null) {
                batchPlanRelTableDTO1.setDatasourceName(datasourceDTO.getDatasourceName());
            }
        }
        return list;
    }
}
