package com.hand.hdsp.quality.infra.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.PlanGroupDTO;
import com.hand.hdsp.quality.domain.repository.PlanGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2021/03/15 14:47
 * @since 1.0
 */
@Slf4j
@ImportValidators(value = {@ImportValidator(templateCode = TemplateCodeConstants.TEMPLATE_CODE_BATCH_PLAN, sheetIndex = 0)})
public class PlanGroupValidator extends BatchValidatorHandler {
    private final ObjectMapper objectMapper;
    private final PlanGroupRepository planGroupRepository;

    public PlanGroupValidator(ObjectMapper objectMapper, PlanGroupRepository planGroupRepository) {
        this.objectMapper = objectMapper;
        this.planGroupRepository = planGroupRepository;
    }

    @Override
    public boolean validate(List<String> data) {
        // 设置租户Id
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        try {
            for (int i = 0; i < data.size(); i++) {
                PlanGroupDTO planGroupDTO = objectMapper.readValue(data.get(i), PlanGroupDTO.class);
                planGroupDTO.setTenantId(tenantId);
                //不检验分组编码是否重复，不存在则新建，存在则直接使用
//                List<PlanGroup> list = planGroupRepository.selectByCondition(Condition.builder(PlanGroup.class)
//                        .andWhere(Sqls.custom()
//                                .andEqualTo(PlanGroup.FIELD_GROUP_CODE, planGroupDTO.getGroupCode())
//                                .andEqualTo(PlanGroup.FIELD_TENANT_ID, tenantId))
//                        .build());
//                //分组编码存在
//                if (CollectionUtils.isNotEmpty(list)) {
//                    addErrorMsg(i, "方案分组编码已存在");
//                    return false;
//                }

//                List<PlanGroup> list2 = planGroupRepository.selectByCondition(Condition.builder(RuleGroup.class)
//                        .andWhere(Sqls.custom()
//                                .andEqualTo(PlanGroup.FIELD_GROUP_NAME, planGroupDTO.getGroupName())
//                                .andEqualTo(PlanGroup.FIELD_TENANT_ID, tenantId))
//                        .build());
//                //分组名称存在
//                if (CollectionUtils.isNotEmpty(list2)) {
//                    addErrorMsg(i, "方案分组名称已存在");
//                    return false;
//                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
