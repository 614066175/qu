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
    private final RuleGroupRepository ruleGroupRepository;

    public RuleGroupValidator(ObjectMapper objectMapper, RuleGroupRepository ruleGroupRepository) {
        this.objectMapper = objectMapper;
        this.ruleGroupRepository = ruleGroupRepository;
    }


    @Override
    public boolean validate(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        try {
            for (int i = 0; i < data.size(); i++) {
                RuleGroupDTO ruleGroupDTO = objectMapper.readValue(data.get(i), RuleGroupDTO.class);
                ruleGroupDTO.setTenantId(tenantId);
                List<RuleGroup> list = ruleGroupRepository.selectByCondition(Condition.builder(RuleGroup.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(RuleGroup.FIELD_GROUP_CODE, ruleGroupDTO.getGroupCode())
                                .andEqualTo(RuleGroup.FIELD_TENANT_ID, tenantId)
                                .andEqualTo(RuleGroup.FIELD_PROJECT_ID, projectId))
                        .build());
                //分组编码存在
                if (CollectionUtils.isNotEmpty(list)) {
                    addErrorMsg(i, "分组编码已存在");
                    return false;
                }

                List<RuleGroup> list2 = ruleGroupRepository.selectByCondition(Condition.builder(RuleGroup.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(RuleGroup.FIELD_GROUP_NAME, ruleGroupDTO.getGroupName())
                                .andEqualTo(RuleGroup.FIELD_TENANT_ID, tenantId)
                                .andEqualTo(RuleGroup.FIELD_PROJECT_ID, projectId))
                        .build());
                //分组名称存在
                if (CollectionUtils.isNotEmpty(list2)) {
                    addErrorMsg(i, "分组名称已存在");
                    return false;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
