package com.hand.hdsp.quality.infra.batchimport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.domain.repository.DataFieldRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;

/**
 * @author wsl
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_FIELD_STANDARD)
public class DataFieldStandardBatchImportServiceImpl implements IBatchImportService {

    private final ObjectMapper objectMapper;
    private final DataFieldRepository dataFieldRepository;

    public DataFieldStandardBatchImportServiceImpl(ObjectMapper objectMapper, DataFieldRepository dataFieldRepository) {
        this.objectMapper = objectMapper;
        this.dataFieldRepository = dataFieldRepository;
    }

    @Override
    public Boolean doImport(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        List<DataFieldDTO> dataFieldDTOList = new ArrayList<>(data.size());
        try {
            for (String json : data) {
                DataFieldDTO dataFieldDTO = objectMapper.readValue(json, DataFieldDTO.class);
                dataFieldDTO.setTenantId(tenantId);
                dataFieldDTOList.add(dataFieldDTO);
            }
        } catch (IOException e) {
            // 失败
            log.error("StandardDoc Object data:{}", data);
            log.error("StandardDoc Object Read Json Error", e);
            return false;
        }
        dataFieldRepository.batchImport(dataFieldDTOList);
        return true;
    }
}
