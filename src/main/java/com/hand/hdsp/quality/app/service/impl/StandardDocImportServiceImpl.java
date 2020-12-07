package com.hand.hdsp.quality.app.service.impl;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.StandardDocDTO;
import com.hand.hdsp.quality.domain.repository.StandardDocRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.imported.app.service.IDoImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;

/**
 * @author StoneHell
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLAT_CODE_STANDARD_DOC)
public class StandardDocImportServiceImpl implements IDoImportService {

    private final ObjectMapper objectMapper;
    private final StandardDocRepository standardDocRepository;

    public StandardDocImportServiceImpl(ObjectMapper objectMapper, StandardDocRepository standardDocRepository) {
        this.objectMapper = objectMapper;
        this.standardDocRepository = standardDocRepository;
    }

    @Override
    public Boolean doImport(String data) {
        StandardDocDTO standardDocDTO;
        try {
            standardDocDTO = objectMapper.readValue(data, StandardDocDTO.class);
        } catch (IOException e) {
            log.error("StandardDoc Object data:{}", data);
            log.error("StandardDoc Object Read Json Error", e);
            // 失败
            return false;
        }
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        if (tenantId != 0) {
            standardDocDTO.setTenantId(tenantId);
        }
        // 插入数据
        standardDocRepository.insertDTOSelective(standardDocDTO);
        return true;
    }
}
