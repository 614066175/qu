package com.hand.hdsp.quality.infra.validation;

import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;

/**
 * description
 *
 * @author XIN.SHENG01@HAND-CHINA.COM 2022/11/22 20:52
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_ROOT, sheetIndex = 0)})
public class RootGroupValidator extends BatchValidatorHandler {

    @Override
    public boolean validate(List<String> data) {
        return true;
    }
}
