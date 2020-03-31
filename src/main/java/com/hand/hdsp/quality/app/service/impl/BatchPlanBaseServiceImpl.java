package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.BatchPlanBaseService;
import com.hand.hdsp.quality.domain.entity.BatchResult;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import io.choerodon.core.exception.CommonException;
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

    public BatchPlanBaseServiceImpl(BatchPlanBaseRepository batchPlanBaseRepository,
                                    BatchPlanTableRepository batchPlanTableRepository,
                                    BatchPlanTableLineRepository batchPlanTableLineRepository,
                                    BatchPlanFieldRepository batchPlanFieldRepository,
                                    BatchPlanFieldLineRepository batchPlanFieldLineRepository,
                                    BatchPlanRelTableRepository batchPlanRelTableRepository,
                                    BatchPlanRelTableLineRepository batchPlanRelTableLineRepository,
                                    PlanWarningLevelRepository planWarningLevelRepository,
                                    BatchResultRepository batchResultRepository) {
        this.batchPlanBaseRepository = batchPlanBaseRepository;
        this.batchPlanTableRepository = batchPlanTableRepository;
        this.batchPlanTableLineRepository = batchPlanTableLineRepository;
        this.batchPlanFieldRepository = batchPlanFieldRepository;
        this.batchPlanFieldLineRepository = batchPlanFieldLineRepository;
        this.batchPlanRelTableRepository = batchPlanRelTableRepository;
        this.batchPlanRelTableLineRepository = batchPlanRelTableLineRepository;
        this.planWarningLevelRepository = planWarningLevelRepository;
        this.batchResultRepository = batchResultRepository;
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
}