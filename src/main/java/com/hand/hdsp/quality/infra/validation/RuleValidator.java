package com.hand.hdsp.quality.infra.validation;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/24 9:25
 * @since 1.0
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_RULE)})
public class RuleValidator extends BatchValidatorHandler {

    private final ObjectMapper objectMapper;

    public RuleValidator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean validate(List<String> data) {
        System.out.println(data);
        return false;
    }
}
