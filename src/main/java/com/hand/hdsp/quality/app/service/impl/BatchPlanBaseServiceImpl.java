package com.hand.hdsp.quality.app.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hand.hdsp.quality.api.dto.*;
import com.hand.hdsp.quality.app.service.BatchPlanBaseService;
import com.hand.hdsp.quality.domain.entity.*;
import com.hand.hdsp.quality.domain.repository.*;
import com.hand.hdsp.quality.infra.constant.ErrorCode;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.feign.DatasourceFeign;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.util.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>批数据方案-基础配置表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Service
public class BatchPlanBaseServiceImpl implements BatchPlanBaseService {

    private final BatchPlanBaseRepository batchPlanBaseRepository;
    private final BatchPlanRepository batchPlanRepository;
    private final BatchPlanTableRepository batchPlanTableRepository;
    private final BatchPlanTableLineRepository batchPlanTableLineRepository;
    private final BatchPlanFieldRepository batchPlanFieldRepository;
    private final BatchPlanFieldLineRepository batchPlanFieldLineRepository;
    private final BatchPlanRelTableRepository batchPlanRelTableRepository;
    private final BatchResultRepository batchResultRepository;
    private final DatasourceFeign datasourceFeign;

    public BatchPlanBaseServiceImpl(BatchPlanBaseRepository batchPlanBaseRepository,
                                    BatchPlanRepository batchPlanRepository,
                                    BatchPlanTableRepository batchPlanTableRepository,
                                    BatchPlanTableLineRepository batchPlanTableLineRepository,
                                    BatchPlanFieldRepository batchPlanFieldRepository,
                                    BatchPlanFieldLineRepository batchPlanFieldLineRepository,
                                    BatchPlanRelTableRepository batchPlanRelTableRepository,
                                    BatchResultRepository batchResultRepository,
                                    DatasourceFeign datasourceFeign) {
        this.batchPlanBaseRepository = batchPlanBaseRepository;
        this.batchPlanRepository = batchPlanRepository;
        this.batchPlanTableRepository = batchPlanTableRepository;
        this.batchPlanTableLineRepository = batchPlanTableLineRepository;
        this.batchPlanFieldRepository = batchPlanFieldRepository;
        this.batchPlanFieldLineRepository = batchPlanFieldLineRepository;
        this.batchPlanRelTableRepository = batchPlanRelTableRepository;
        this.batchResultRepository = batchResultRepository;
        this.datasourceFeign = datasourceFeign;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(BatchPlanBaseDTO batchPlanBaseDTO) {
        List<BatchResultDTO> batchResultDTOList = batchResultRepository.selectDTO(
                BatchResult.FIELD_PLAN_ID, batchPlanBaseDTO.getPlanId());
        if (CollectionUtils.isNotEmpty(batchResultDTOList)
                && PlanConstant.PlanStatus.RUNNING.equals(batchResultDTOList.get(0).getPlanStatus())) {
            throw new CommonException(ErrorCode.CAN_NOT_DELETE);
        }
        List<BatchPlanTableDTO> batchPlanTableDTOList =
                batchPlanTableRepository.selectDTO(
                        BatchPlanTable.FIELD_PLAN_BASE_ID, batchPlanBaseDTO.getPlanBaseId());
        if (batchPlanTableDTOList != null) {
            for (BatchPlanTableDTO batchPlanTableDTO : batchPlanTableDTOList) {
                List<BatchPlanTableLineDTO> batchPlanTableLineDTOList =
                        batchPlanTableLineRepository.selectDTO(
                                BatchPlanTableLine.FIELD_PLAN_RULE_ID, batchPlanTableDTO.getPlanRuleId());
                if (batchPlanTableLineDTOList != null) {

                    batchPlanTableLineRepository.deleteByParentId(batchPlanTableDTO.getPlanRuleId());
                }
            }
            batchPlanTableRepository.deleteByParentId(batchPlanBaseDTO.getPlanBaseId());
        }

        List<BatchPlanFieldDTO> batchPlanFieldDTOList = batchPlanFieldRepository.selectDTO(
                BatchPlanField.FIELD_PLAN_BASE_ID, batchPlanBaseDTO.getPlanBaseId());
        if (batchPlanFieldDTOList != null) {
            for (BatchPlanFieldDTO batchPlanFieldDTO : batchPlanFieldDTOList) {
                batchPlanFieldLineRepository.deleteByParentId(batchPlanFieldDTO.getPlanRuleId());
            }
            batchPlanFieldRepository.deleteByParentId(batchPlanBaseDTO.getPlanBaseId());
        }

        List<BatchPlanRelTableDTO> batchPlanRelTableDTOList = batchPlanRelTableRepository.selectDTO(
                BatchPlanRelTable.FIELD_PLAN_BASE_ID, batchPlanBaseDTO.getPlanBaseId());
        if (batchPlanRelTableDTOList != null) {
            batchPlanRelTableRepository.deleteByParentId(batchPlanBaseDTO.getPlanBaseId());
        }
        return batchPlanBaseRepository.deleteByPrimaryKey(batchPlanBaseDTO);
    }

    @Override
    public BatchPlanBaseDTO detail(Long planBaseId, Long tenantId) {
        BatchPlanBaseDTO batchPlanBaseDTO = batchPlanBaseRepository.detail(planBaseId);
        ResponseEntity<String> result = datasourceFeign.detail(tenantId, batchPlanBaseDTO.getDatasourceId());
        DatasourceDTO datasourceDTO = ResponseUtils.getResponse(result, new TypeReference<DatasourceDTO>() {
        });
        if (datasourceDTO != null) {
            batchPlanBaseDTO.setDatasourceName(datasourceDTO.getDatasourceName());
        }
        return batchPlanBaseDTO;
    }
}
