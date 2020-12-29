package com.hand.hdsp.quality.app.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.RuleGroupDTO;
import com.hand.hdsp.quality.domain.repository.RuleGroupRepository;
import com.hand.hdsp.quality.infra.constant.TemplateCodeConstants;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/25 10:34
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_RULE,sheetIndex = 1)
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
        List<RuleGroupDTO> ruleGroupDTOList = new ArrayList<>(data.size());
        try {
            for (String json : data) {
                RuleGroupDTO ruleGroupDTO = objectMapper.readValue(json, RuleGroupDTO.class);
                ruleGroupDTO.setTenantId(tenantId);
                ruleGroupDTOList.add(ruleGroupDTO);
            }
        } catch (IOException e) {
            // 失败
            log.error("Permission Object data:{}", data);
            log.error("Permission Object Read Json Error", e);
            return false;
        }
        ruleGroupRepository.batchImport(ruleGroupDTOList);
        return true;
    }
}
