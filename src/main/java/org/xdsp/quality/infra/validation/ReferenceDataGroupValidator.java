package org.xdsp.quality.infra.validation;

import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.xdsp.quality.infra.constant.TemplateCodeConstants;

import java.util.List;

/**
 * 参考数据的通用分组校验
 * @author fuqiang.luo@hand-china.com
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_REFERENCE_DATA, sheetIndex = 0)})
public class ReferenceDataGroupValidator extends BatchValidatorHandler {

    private final ObjectMapper objectMapper;

    public ReferenceDataGroupValidator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean validate(List<String> data) {
        for (int i = 0; i < data.size(); i++) {
            try {
                String json = data.get(i);
                CommonGroup commonGroup = objectMapper.readValue(json, CommonGroup.class);
                String groupName = commonGroup.getGroupName();
                if (StringUtils.isBlank(groupName)) {
                    log.error("分组名称不能为空");
                    addErrorMsg(i, "分组名称不能为空");
                    return false;
                }
                if (StringUtils.length(groupName) > 64) {
                    log.error("分组名称长度不能超过64");
                    addErrorMsg(i, "分组名称长度不能超过64");
                    return false;
                }
            } catch (Exception e) {
                log.error("校验参考数据分组失败", e);
                addErrorMsg(i, e.getMessage());
                return false;
            }
        }
        return true;
    }
}
