package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.BatchPlanRelTableDTO;
import com.hand.hdsp.quality.app.service.BatchPlanRelTableService;
import com.hand.hdsp.quality.domain.entity.BatchPlanBase;
import com.hand.hdsp.quality.domain.repository.BatchPlanBaseRepository;
import com.hand.hdsp.quality.domain.repository.BatchPlanRelTableRepository;
import com.hand.hdsp.quality.infra.util.JsonUtils;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>批数据方案-表间规则表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Service
public class BatchPlanRelTableServiceImpl implements BatchPlanRelTableService {

    private final BatchPlanRelTableRepository batchPlanRelTableRepository;
    private final BatchPlanBaseRepository batchPlanBaseRepository;

    public BatchPlanRelTableServiceImpl(BatchPlanRelTableRepository batchPlanRelTableRepository,
                                         BatchPlanBaseRepository batchPlanBaseRepository) {
        this.batchPlanRelTableRepository = batchPlanRelTableRepository;
        this.batchPlanBaseRepository = batchPlanBaseRepository;
    }

    @Override
    public BatchPlanRelTableDTO detail(Long planRuleId) {
        BatchPlanRelTableDTO dto = batchPlanRelTableRepository.selectDTOByPrimaryKey(planRuleId);
        BatchPlanBase batchPlanBase = batchPlanBaseRepository.selectByPrimaryKey(dto.getPlanBaseId());
        if (batchPlanBase != null) {
            dto.setDatasourceCode(batchPlanBase.getDatasourceCode());
        }
        dto.setWarningLevelList(JsonUtils.json2WarningLevel(dto.getWarningLevel()));
        dto.setRelationshipList(JsonUtils.json2Relationship(dto.getRelationship()));
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(BatchPlanRelTableDTO batchPlanRelTableDTO) {
        if (CollectionUtils.isNotEmpty(batchPlanRelTableDTO.getWarningLevelList())) {
            batchPlanRelTableDTO.setWarningLevel(JsonUtils.object2Json(batchPlanRelTableDTO.getWarningLevelList()));
        }
        if (CollectionUtils.isNotEmpty(batchPlanRelTableDTO.getRelationshipList())) {
            batchPlanRelTableDTO.setRelationship(JsonUtils.object2Json(batchPlanRelTableDTO.getRelationshipList()));
        }
        batchPlanRelTableRepository.insertDTOSelective(batchPlanRelTableDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BatchPlanRelTableDTO batchPlanRelTableDTO) {
        Long tenantId = batchPlanRelTableDTO.getTenantId();
        if (CollectionUtils.isNotEmpty(batchPlanRelTableDTO.getWarningLevelList())) {
            batchPlanRelTableDTO.setWarningLevel(JsonUtils.object2Json(batchPlanRelTableDTO.getWarningLevelList()));
        }
        if (CollectionUtils.isNotEmpty(batchPlanRelTableDTO.getRelationshipList())) {
            batchPlanRelTableDTO.setRelationship(JsonUtils.object2Json(batchPlanRelTableDTO.getRelationshipList()));
        }
        batchPlanRelTableRepository.updateDTOAllColumnWhereTenant(batchPlanRelTableDTO, tenantId);

    }

    @Override
    public Page<BatchPlanRelTableDTO> list(PageRequest pageRequest, BatchPlanRelTableDTO batchPlanRelTableDTO) {
        return batchPlanRelTableRepository.pageAndSortDTO(pageRequest, batchPlanRelTableDTO);
    }

    @Override
    public Page<BatchPlanRelTableDTO> selectDetailList(PageRequest pageRequest, BatchPlanRelTableDTO batchPlanRelTableDTO) {
        Page<BatchPlanRelTableDTO> pages = batchPlanRelTableRepository.pageAndSortDTO(pageRequest, batchPlanRelTableDTO);
        for (BatchPlanRelTableDTO dto : pages.getContent()) {
            dto.setWarningLevelList(JsonUtils.json2WarningLevel(dto.getWarningLevel()));
            dto.setRelationshipList(JsonUtils.json2Relationship(dto.getRelationship()));
        }
        return pages;
    }
}
