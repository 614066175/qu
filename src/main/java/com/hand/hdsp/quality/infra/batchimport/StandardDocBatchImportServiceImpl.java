package com.hand.hdsp.quality.infra.batchimport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.StandardDocDTO;
import com.hand.hdsp.quality.domain.repository.StandardDocRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;

/**
 * @author StoneHell
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_STANDARD_DOC)
public class StandardDocBatchImportServiceImpl implements IBatchImportService {

    private final ObjectMapper objectMapper;
    private final StandardDocRepository standardDocRepository;

    public StandardDocBatchImportServiceImpl(ObjectMapper objectMapper, StandardDocRepository standardDocRepository) {
        this.objectMapper = objectMapper;
        this.standardDocRepository = standardDocRepository;
    }

    @Override
    public Boolean doImport(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        List<StandardDocDTO> standardDocDTOList = new ArrayList<>(data.size());
        try {
            for (String json : data) {
                StandardDocDTO standardDocDTO = objectMapper.readValue(json, StandardDocDTO.class);
                standardDocDTO.setTenantId(tenantId);
                standardDocDTOList.add(standardDocDTO);
            }
        } catch (IOException e) {
            // 失败
            log.error("StandardDoc Object data:{}", data);
            log.error("StandardDoc Object Read Json Error", e);
            return false;
        }
        standardDocRepository.batchImport(standardDocDTOList);
        return true;
    }
}
