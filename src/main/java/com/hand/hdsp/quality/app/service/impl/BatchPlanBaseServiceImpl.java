package com.hand.hdsp.quality.app.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.BatchPlanBaseService;
import com.hand.hdsp.quality.domain.entity.*;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.constant.TableNameConstant;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import io.choerodon.core.exception.CommonException;
import org.hzero.core.util.ResponseUtils;
import org.springframework.http.ResponseEntity;
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
    private final PlanWarningLevelRepository planWarningLevelRepository;
    private final BatchResultRepository batchResultRepository;
    private final DatasourceFeign datasourceFeign;

    public BatchPlanBaseServiceImpl(BatchPlanBaseRepository batchPlanBaseRepository,
                                    BatchPlanTableRepository batchPlanTableRepository,
                                    BatchPlanTableLineRepository batchPlanTableLineRepository,
                                    BatchPlanFieldRepository batchPlanFieldRepository,
                                    BatchPlanFieldLineRepository batchPlanFieldLineRepository,
                                    BatchPlanRelTableRepository batchPlanRelTableRepository,
                                    BatchPlanRelTableLineRepository batchPlanRelTableLineRepository,
                                    PlanWarningLevelRepository planWarningLevelRepository,
                                    BatchResultRepository batchResultRepository,
                                    DatasourceFeign datasourceFeign) {
        this.batchPlanBaseRepository = batchPlanBaseRepository;
        this.batchPlanTableRepository = batchPlanTableRepository;
        this.batchPlanTableLineRepository = batchPlanTableLineRepository;
        this.batchPlanFieldRepository = batchPlanFieldRepository;
        this.batchPlanFieldLineRepository = batchPlanFieldLineRepository;
        this.batchPlanRelTableRepository = batchPlanRelTableRepository;
        this.batchPlanRelTableLineRepository = batchPlanRelTableLineRepository;
        this.planWarningLevelRepository = planWarningLevelRepository;
        this.batchResultRepository = batchResultRepository;
        this.datasourceFeign = datasourceFeign;
    }

    @Override
    public int delete(BatchPlanBaseDTO batchPlanBaseDTO) {
        List<BatchResultDTO> batchResultDTOList = batchResultRepository.selectDTO(
                BatchResult.FIELD_PLAN_ID, batchPlanBaseDTO.getPlanId());
        if (batchResultDTOList != null) {
            if (batchResultDTOList.get(0).getPlanStatus().equals(PlanConstant.PlanStatus.RUNNING)){
                throw new CommonException(ErrorCode.CAN_NOT_DELETE);
            }
        }
        List<BatchPlanTableDTO> batchPlanTableDTOList =
                batchPlanTableRepository.selectDTO(
                        BatchPlanTable.FIELD_PLAN_BASE_ID, batchPlanBaseDTO.getPlanBaseId());
        if (batchPlanTableDTOList != null) {
            for (BatchPlanTableDTO batchPlanTableDTO : batchPlanTableDTOList) {
                List<BatchPlanTableLineDTO> batchPlanTableLineDTOList =
                        batchPlanTableLineRepository.selectDTO(
                                BatchPlanTableLine.FIELD_PLAN_TABLE_ID, batchPlanTableDTO.getPlanTableId());
                if (batchPlanTableLineDTOList != null) {
                    for (BatchPlanTableLineDTO batchPlanTableLineDTO : batchPlanTableLineDTOList) {
                        List<PlanWarningLevel> planWarningLevelList =
                                planWarningLevelRepository.select(PlanWarningLevel.builder()
                                        .sourceId(batchPlanTableLineDTO.getPlanTableLineId())
                                        .sourceType(TableNameConstant.XQUA_BATCH_PLAN_TABLE_LINE).build());
                        if (planWarningLevelList != null) {
                            for (PlanWarningLevel planWarningLevel :
                                    planWarningLevelList) {
                                planWarningLevelRepository.delete(planWarningLevel);
                            }
                        }
                        batchPlanTableLineRepository.deleteDTO(batchPlanTableLineDTO);
                    }
                }
                batchPlanTableRepository.deleteDTO(batchPlanTableDTO);
            }
        }

        List<BatchPlanFieldDTO> batchPlanFieldDTOList = batchPlanFieldRepository.selectDTO(
                BatchPlanField.FIELD_PLAN_BASE_ID, batchPlanBaseDTO.getPlanBaseId());
        if (batchPlanFieldDTOList != null) {
            for (BatchPlanFieldDTO batchPlanFieldDTO : batchPlanFieldDTOList) {
                List<BatchPlanFieldLineDTO> batchPlanFieldLineDTOList =
                        batchPlanFieldLineRepository.selectDTO(BatchPlanFieldLine.FIELD_PLAN_FIELD_ID,
                                batchPlanFieldDTO.getPlanFieldId());
                if (batchPlanFieldLineDTOList != null) {
                    for (BatchPlanFieldLineDTO batchPlanFieldLineDTO :
                            batchPlanFieldLineDTOList) {
                        batchPlanFieldLineRepository.deleteDTO(batchPlanFieldLineDTO);
                    }
                }
                batchPlanFieldRepository.deleteDTO(batchPlanFieldDTO);
            }
        }

        List<BatchPlanRelTableDTO> batchPlanRelTableDTOList = batchPlanRelTableRepository.selectDTO(
                BatchPlanRelTable.FIELD_PLAN_BASE_ID, batchPlanBaseDTO.getPlanBaseId());
        if (batchPlanRelTableDTOList != null) {
            for (BatchPlanRelTableDTO batchPlanRelTableDTO : batchPlanRelTableDTOList) {
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
                batchPlanRelTableRepository.deleteDTO(batchPlanRelTableDTO);
            }
        }
        return batchPlanBaseRepository.deleteByPrimaryKey(batchPlanBaseDTO);
    }

    @Override
    public BatchPlanBaseDTO detail(Long planBaseId, Long tenantId) {
        BatchPlanBaseDTO batchPlanBaseDTO = batchPlanBaseRepository.selectDTOByPrimaryKey(planBaseId);
        ResponseEntity<String> result = datasourceFeign.detail(tenantId, batchPlanBaseDTO.getDatasourceId());
        DatasourceDTO datasourceDTO = ResponseUtils.getResponse(result, new TypeReference<DatasourceDTO>() {
        }, (httpStatus, response) -> {
            throw new CommonException(response);
        }, exceptionResponse -> {
            throw new CommonException(exceptionResponse.getMessage());
        });
        if (datasourceDTO != null) {
            batchPlanBaseDTO.setDatasourceName(datasourceDTO.getDatasourceName());
        }
        return batchPlanBaseDTO;
    }
}