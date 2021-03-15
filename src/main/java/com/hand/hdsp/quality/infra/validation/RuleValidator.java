package com.hand.hdsp.quality.infra.validation;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.domain.entity.Rule;
import com.hand.hdsp.quality.domain.repository.RuleRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/24 9:25
 * @since 1.0
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_RULE,sheetIndex = 1)})
public class RuleValidator extends BatchValidatorHandler {

    private final ObjectMapper objectMapper;
    private final RuleRepository ruleRepository;

    public RuleValidator(ObjectMapper objectMapper, RuleRepository ruleRepository) {
        this.objectMapper = objectMapper;
        this.ruleRepository = ruleRepository;
    }

    @Override
    public boolean validate(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        try {
            for (int i = 0; i < data.size(); i++) {
                RuleDTO ruleDTO = objectMapper.readValue(data.get(i), RuleDTO.class);
                ruleDTO.setTenantId(tenantId);
                List<RuleDTO> list = ruleRepository.selectDTOByCondition(Condition.builder(Rule.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(Rule.FIELD_RULE_CODE, ruleDTO.getRuleCode())
                                .andEqualTo(Rule.FIELD_TENANT_ID, tenantId))
                        .build());
                //标准编码存在
                if (CollectionUtils.isNotEmpty(list)) {
                    addErrorMsg(i, "规则编码已存在");
                    return false;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
