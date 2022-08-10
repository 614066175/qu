package com.hand.hdsp.quality.infra.batchimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.core.util.ProjectHelper;
import com.hand.hdsp.quality.api.dto.RuleGroupDTO;
import com.hand.hdsp.quality.domain.repository.RuleGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/25 10:34
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_RULE,sheetIndex = 0)
public class RuleGroupBatchImportServiceImpl implements IBatchImportService {
    private final ObjectMapper objectMapper;
    private final RuleGroupRepository ruleGroupRepository;

    public RuleGroupBatchImportServiceImpl(ObjectMapper objectMapper, RuleGroupRepository ruleGroupRepository) {
        this.objectMapper = objectMapper;
        this.ruleGroupRepository = ruleGroupRepository;
    }


    @Override
    public Boolean doImport(List<String> data) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long projectId = ProjectHelper.getProjectId();
        List<RuleGroupDTO> ruleGroupDTOList = new ArrayList<>(data.size());
        try {
            for (String json : data) {
                RuleGroupDTO ruleGroupDTO = objectMapper.readValue(json, RuleGroupDTO.class);
                ruleGroupDTO.setParentGroupId(0L);
                ruleGroupDTO.setTenantId(tenantId);
                ruleGroupDTO.setProjectId(projectId);
                ruleGroupDTOList.add(ruleGroupDTO);
            }
        } catch (IOException e) {
            // 失败
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }
        ruleGroupRepository.batchInsertDTOSelective(ruleGroupDTOList);
        return true;
    }
}
