package com.hand.hdsp.quality.infra.validation;

import java.util.List;

import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import lombok.extern.slf4j.Slf4j;

import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;

/**
 * 参考数据的通用分组校验
 * @author fuqiang.luo@hand-china.com
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_REFERENCE_DATA, sheetIndex = 0)})
public class ReferenceDataGroupValidator extends BatchValidatorHandler {


    @Override
    public boolean validate(List<String> data) {
        // 暂不做校验
        return true;
    }
}
