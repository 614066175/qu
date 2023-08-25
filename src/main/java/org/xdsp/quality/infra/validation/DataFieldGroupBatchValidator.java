package org.xdsp.quality.infra.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.xdsp.quality.api.dto.StandardGroupDTO;

import java.io.IOException;
import java.util.List;

@Slf4j
@Deprecated
//@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_FIELD_STANDARD, sheetIndex = 0)})
public class DataFieldGroupBatchValidator extends BatchValidatorHandler {

    private final ObjectMapper objectMapper;

    public DataFieldGroupBatchValidator(ObjectMapper objectMapper) {
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
