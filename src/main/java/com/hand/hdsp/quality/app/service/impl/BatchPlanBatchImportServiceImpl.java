package com.hand.hdsp.quality.app.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.BatchPlanDTO;
import com.hand.hdsp.quality.domain.repository.BatchPlanRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/21 19:37
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_BATCH_PLAN)
public class BatchPlanBatchImportServiceImpl implements IBatchImportService {
    private final ObjectMapper objectMapper;

    private final BatchPlanRepository batchPlanRepository;

    public BatchPlanBatchImportServiceImpl(ObjectMapper objectMapper, BatchPlanRepository batchPlanRepository) {
        this.objectMapper = objectMapper;
        this.batchPlanRepository = batchPlanRepository;
    }


    @Override
    public Boolean doImport(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        List<BatchPlanDTO> batchPlanDTOList = new ArrayList<>(data.size());
        try {
            for (String json : data) {
                BatchPlanDTO batchPlanDTO = objectMapper.readValue(json, BatchPlanDTO.class);
                batchPlanDTO.setTenantId(tenantId);
                batchPlanDTOList.add(batchPlanDTO);
            }
        } catch (IOException e) {
            // 失败
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }
        batchPlanRepository.batchImport(batchPlanDTOList);
        return true;
    }
}
