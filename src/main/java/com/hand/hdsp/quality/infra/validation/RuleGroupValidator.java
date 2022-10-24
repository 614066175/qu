package com.hand.hdsp.quality.infra.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.RuleGroupDTO;
import com.hand.hdsp.quality.domain.entity.RuleGroup;
import com.hand.hdsp.quality.domain.repository.RuleGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2021/03/15 14:16
 * @since 1.0
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_RULE, sheetIndex = 0)})
public class RuleGroupValidator extends BatchValidatorHandler {
    private final ObjectMapper objectMapper;

    public RuleGroupValidator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public boolean validate(List<String> data) {
        try {
            for (int i = 0; i < data.size(); i++) {
                RuleGroupDTO ruleGroupDTO = objectMapper.readValue(data.get(i), RuleGroupDTO.class);
                //校验分组
                if (StringUtils.isEmpty(ruleGroupDTO.getGroupCode())) {
                    addErrorMsg(i, "未导入分组编码");
                    return false;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
