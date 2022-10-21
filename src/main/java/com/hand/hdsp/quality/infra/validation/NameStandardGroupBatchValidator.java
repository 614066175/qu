package com.hand.hdsp.quality.infra.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;

import java.io.IOException;
import java.util.List;

@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_NAME_STANDARD, sheetIndex = 0)})
public class NameStandardGroupBatchValidator extends BatchValidatorHandler {

    private final ObjectMapper objectMapper;

    public NameStandardGroupBatchValidator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean validate(List<String> data) {
        //校验
        try {
            for (int i = 0; i < data.size(); i++) {
                StandardGroupDTO standardGroupDTO = objectMapper.readValue(data.get(i), StandardGroupDTO.class);
                //校验分组
                if (StringUtils.isEmpty(standardGroupDTO.getGroupCode())) {
                    addErrorMsg(i, "未导入分组编码");
                    return false;
                }
            }
        } catch (IOException e) {
            log.info(e.getMessage());
            return false;
        }
        return true;
    }
}
