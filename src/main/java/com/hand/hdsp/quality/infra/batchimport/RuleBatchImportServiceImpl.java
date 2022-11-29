package com.hand.hdsp.quality.infra.batchimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.api.dto.RuleGroupDTO;
import com.hand.hdsp.quality.api.dto.RuleLineDTO;
import com.hand.hdsp.quality.domain.entity.RuleGroup;
import com.hand.hdsp.quality.domain.repository.RuleGroupRepository;
import com.hand.hdsp.quality.domain.repository.RuleLineRepository;
import com.hand.hdsp.quality.domain.repository.RuleRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/09 16:41
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_RULE, sheetIndex = 1)
public class RuleBatchImportServiceImpl implements IBatchImportService {
    private final ObjectMapper objectMapper;
    private final RuleRepository ruleRepository;
    private final RuleGroupRepository ruleGroupRepository;
    private final RuleLineRepository ruleLineRepository;

    public RuleBatchImportServiceImpl(ObjectMapper objectMapper,
                                      RuleRepository ruleRepository,
                                      RuleGroupRepository ruleGroupRepository,
                                      RuleLineRepository ruleLineRepository) {
        this.objectMapper = objectMapper;
        this.ruleRepository = ruleRepository;
        this.ruleGroupRepository = ruleGroupRepository;
        this.ruleLineRepository = ruleLineRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean doImport(List<String> data) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        try {
            for (String json : data) {
                RuleDTO ruleDTO = objectMapper.readValue(json, RuleDTO.class);
                //导入分组id
                List<RuleGroupDTO> ruleGroupDTOS = ruleGroupRepository.selectDTOByCondition(Condition.builder(RuleGroup.class).andWhere(Sqls.custom()
                        .andEqualTo(RuleGroup.FIELD_TENANT_ID, tenantId)
                        .andEqualTo(RuleGroup.FIELD_PROJECT_ID, projectId)
                        .andEqualTo(RuleGroup.FIELD_GROUP_CODE, ruleDTO.getGroupCode())
                ).build());
                if (CollectionUtils.isNotEmpty(ruleGroupDTOS)) {
                    ruleDTO.setGroupId(ruleGroupDTOS.get(0).getGroupId());
                }
                ruleDTO.setTenantId(tenantId);
                ruleDTO.setProjectId(projectId);
                ruleRepository.insertDTOSelective(ruleDTO);
                //导入校验项
                ruleLineRepository.insertDTOSelective(RuleLineDTO.builder()
                        .ruleId(ruleDTO.getRuleId())
                        .checkWay(ruleDTO.getCheckWay())
                        .checkItem(ruleDTO.getCheckItem())
                        .compareWay(ruleDTO.getCompareWay())
                        .regularExpression(ruleDTO.getRegularExpression())
                        .warningLevel(ruleDTO.getWarningLevel())
                        .tenantId(tenantId)
                        .projectId(projectId)
                        .build());
            }
        } catch (IOException e) {
            // 失败
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }

        return true;
    }
}
