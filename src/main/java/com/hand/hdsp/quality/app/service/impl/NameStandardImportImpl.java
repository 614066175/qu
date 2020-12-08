package com.hand.hdsp.quality.app.service.impl;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.NameStandardDTO;
import com.hand.hdsp.quality.domain.repository.NameStandardRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.imported.app.service.IDoImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;

/**
 * <p>
 * description
 * </p>
 *
 * @author 张鹏 2020/12/7 19:38
 * @since 1.0.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_NAME_STANDARD)
public class NameStandardImportImpl implements IDoImportService {

    private final ObjectMapper objectMapper;
    private final NameStandardRepository nameStandardRepository;

    public NameStandardImportImpl(ObjectMapper objectMapper,
                                  NameStandardRepository nameStandardRepository) {
        this.objectMapper = objectMapper;
        this.nameStandardRepository = nameStandardRepository;
    }

    @Override
    public Boolean doImport(String data) {
        NameStandardDTO nameStandardDTO;
        try{
            nameStandardDTO=objectMapper.readValue(data, NameStandardDTO.class);
        }catch (IOException e) {
            log.error("data:{}", data);
            log.error("Read Json Error", e);
            // 失败
            return false;
        }
        nameStandardDTO.setTenantId(DetailsHelper.getUserDetails().getTenantId());
        nameStandardRepository.importStandard(nameStandardDTO);
        return true;
    }
}
