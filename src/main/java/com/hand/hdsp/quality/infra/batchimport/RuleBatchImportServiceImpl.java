package com.hand.hdsp.quality.infra.batchimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.domain.entity.Rule;
import com.hand.hdsp.quality.domain.repository.RuleGroupRepository;
import com.hand.hdsp.quality.domain.repository.RuleRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

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
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_RULE,sheetIndex = 1)
public class RuleBatchImportServiceImpl implements IBatchImportService {
    private final ObjectMapper objectMapper;
    private final RuleRepository ruleRepository;
    private final RuleGroupRepository ruleGroupRepository;

    public RuleBatchImportServiceImpl(ObjectMapper objectMapper,
                                      RuleRepository ruleRepository,
                                      RuleGroupRepository ruleGroupRepository) {
        this.objectMapper = objectMapper;
        this.ruleRepository = ruleRepository;
        this.ruleGroupRepository = ruleGroupRepository;
    }

    @Override
    public Boolean doImport(List<String> data) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        List<RuleDTO> ruleDTOList = new ArrayList<>(data.size());
        try {
            for (String json : data) {
                RuleDTO ruleDTO = objectMapper.readValue(json, RuleDTO.class);
                //导入分组id
                List<RuleDTO> ruleDTOS = ruleRepository.selectDTOByCondition(Condition.builder(Rule.class).andWhere(Sqls.custom()
                        .andEqualTo(Rule.FIELD_TENANT_ID,tenantId)
                        .andEqualTo(Rule.FIELD_PROJECT_ID,projectId)
                        .andEqualTo(Rule.FIELD_RULE_CODE,ruleDTO.getGroupCode())
                ).build());
                if(CollectionUtils.isNotEmpty(ruleDTOS)){
                    ruleDTO.setGroupId(ruleDTOS.get(0).getGroupId());
                }
                ruleDTO.setTenantId(tenantId);
                ruleDTO.setProjectId(projectId);
                ruleDTOList.add(ruleDTO);
            }
        } catch (IOException e) {
            // 失败
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }
        ruleRepository.batchImport(ruleDTOList);
        return true;
    }
}
