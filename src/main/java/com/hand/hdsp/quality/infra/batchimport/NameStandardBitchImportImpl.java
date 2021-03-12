package com.hand.hdsp.quality.infra.batchimport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.NameStandardDTO;
import com.hand.hdsp.quality.domain.repository.NameStandardRepository;
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
 * @author 张鹏 2020/12/7 19:56
 * @since 1.0.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_NAME_STANDARD)
public class NameStandardBitchImportImpl implements IBatchImportService {

    private final ObjectMapper objectMapper;
    private final NameStandardRepository nameStandardRepository;

    public NameStandardBitchImportImpl(ObjectMapper objectMapper,
                                  NameStandardRepository nameStandardRepository) {
        this.objectMapper = objectMapper;
        this.nameStandardRepository = nameStandardRepository;
    }

    @Override
    public Boolean doImport(List<String> data) {
        List<NameStandardDTO> nameStandardDTOList = new ArrayList<>(data.size());
        try{
            for (String json:data){
                NameStandardDTO nameStandardDTO = objectMapper.readValue(json,NameStandardDTO.class);
                nameStandardDTO.setTenantId(DetailsHelper.getUserDetails().getTenantId());
                nameStandardDTOList.add(nameStandardDTO);
            }
        }catch (IOException e){
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }
        nameStandardRepository.batchImportStandard(nameStandardDTOList);
        return true;
    }
}
