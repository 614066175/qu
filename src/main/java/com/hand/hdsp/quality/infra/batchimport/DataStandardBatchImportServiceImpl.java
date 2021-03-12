package com.hand.hdsp.quality.infra.batchimport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.domain.repository.DataStandardRepository;
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
 * @author lgl 2020/12/04 15:26
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_DATA_STANDARD)
public class DataStandardBatchImportServiceImpl implements IBatchImportService {

    private final ObjectMapper objectMapper;

    private final DataStandardRepository dataStandardRepository;

    public DataStandardBatchImportServiceImpl(ObjectMapper objectMapper, DataStandardRepository dataStandardRepository) {
        this.objectMapper = objectMapper;
        this.dataStandardRepository = dataStandardRepository;
    }

    @Override
    public Boolean doImport(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        List<DataStandardDTO> dataStandardDTOList = new ArrayList<>(data.size());
        try {
            for (String json : data) {
                DataStandardDTO dataStandardDTO = objectMapper.readValue(json, DataStandardDTO.class);
                dataStandardDTO.setTenantId(tenantId);
                dataStandardDTOList.add(dataStandardDTO);
            }
        } catch (IOException e) {
            // 失败
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }
        dataStandardRepository.batchImport(dataStandardDTOList);
        return true;
    }
}
