package com.hand.hdsp.quality.app.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.BatchPlanBaseService;
import com.hand.hdsp.quality.domain.entity.BatchResult;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.converter.BatchPlanBaseConverter;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import io.choerodon.core.exception.CommonException;
import org.hzero.core.util.ResponseUtils;
import org.springframework.http.ResponseEntity;
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
    private final PlanWarningLevelRepository planWarningLevelRepository;
    private final BatchResultRepository batchResultRepository;
    private final DatasourceFeign datasourceFeign;
    private final BatchPlanBaseConverter batchPlanBaseConverter;

    public BatchPlanBaseServiceImpl(BatchPlanBaseRepository batchPlanBaseRepository,
                                    BatchPlanTableRepository batchPlanTableRepository,
                                    BatchPlanTableLineRepository batchPlanTableLineRepository,
                                    BatchPlanFieldRepository batchPlanFieldRepository,
                                    BatchPlanFieldLineRepository batchPlanFieldLineRepository,
                                    BatchPlanRelTableRepository batchPlanRelTableRepository,
                                    BatchPlanRelTableLineRepository batchPlanRelTableLineRepository,
                                    PlanWarningLevelRepository planWarningLevelRepository,
                                    BatchResultRepository batchResultRepository,
                                    DatasourceFeign datasourceFeign,
                                    BatchPlanBaseConverter batchPlanBaseConverter) {
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
        this.batchPlanBaseConverter = batchPlanBaseConverter;
    }

    @Override
    public int delete(BatchPlanBaseDTO batchPlanBaseDTO) {
        if (batchResultRepository.selectDTO(
                BatchResult.FIELD_PLAN_ID, batchPlanBaseDTO.getPlanId()).get(0)
                .getPlanStatus().equals(PlanConstant.PlanStatus.RUNNING)) {
            throw new CommonException(ErrorCode.CAN_NOT_DELETE);
        }
        if (batchPlanBaseDTO.getBatchPlanTableDTOList() != null) {
            for (BatchPlanTableDTO batchPlanTableDTO : batchPlanBaseDTO.getBatchPlanTableDTOList()) {
                if (batchPlanTableDTO.getBatchPlanTableLineDTOList() != null) {
                    for (BatchPlanTableLineDTO batchPlanTableLineDTO :
                            batchPlanTableDTO.getBatchPlanTableLineDTOList()) {
                        if (batchPlanTableLineDTO.getPlanWarningLevelDTOList() != null) {
                            for (PlanWarningLevelDTO planWarningLevelDTO :
                                    batchPlanTableLineDTO.getPlanWarningLevelDTOList()) {
                                planWarningLevelRepository.deleteDTO(planWarningLevelDTO);
                            }
                        }
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
                if (batchPlanRelTableDTO.getPlanWarningLevelDTOList() != null) {
                    for (PlanWarningLevelDTO planWarningLevelDTO :
                            batchPlanRelTableDTO.getPlanWarningLevelDTOList()) {
                        planWarningLevelRepository.deleteDTO(planWarningLevelDTO);
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