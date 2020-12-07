package com.hand.hdsp.quality.app.service.impl;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.domain.repository.DataStandardRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import com.hand.hdsp.quality.infra.mapper.DataStandardMapper;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.imported.app.service.IDoImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/04 15:20
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLAT_CODE_DATA_STANDARD)
public class DataStandardImportServiceImpl implements IDoImportService {

    private final ObjectMapper objectMapper;

    private final DataStandardRepository dataStandardRepository;

    private final DataStandardMapper dataStandardMapper;

    public DataStandardImportServiceImpl(ObjectMapper objectMapper, DataStandardRepository dataStandardRepository, DataStandardMapper dataStandardMapper) {
        this.objectMapper = objectMapper;
        this.dataStandardRepository = dataStandardRepository;
        this.dataStandardMapper = dataStandardMapper;
    }

    @Override
    public Boolean doImport(String data) {
        DataStandardDTO dataStandardDTO;
        try{
            dataStandardDTO=objectMapper.readValue(data,DataStandardDTO.class);
        }catch (IOException e) {
            log.error("data:{}", data);
            log.error("Read Json Error", e);
            // 失败
            return false;
        }
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        if (tenantId != 0) {
            dataStandardDTO.setTenantId(tenantId);
        }
        //插入数据
        dataStandardRepository.insertDTOSelective(dataStandardDTO);
        return true;
    }
}
