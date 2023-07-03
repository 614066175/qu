package org.xdsp.quality.infra.validation;

import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;

import java.util.List;

/**
 * description
 *
 * @author XIN.SHENG01@HAND-CHINA.COM 2022/11/22 20:52
 */
@Slf4j
@Deprecated
//@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_ROOT, sheetIndex = 0)})
public class RootGroupValidator extends BatchValidatorHandler {

    @Override
    public boolean validate(List<String> data) {
        return true;
    }
}
