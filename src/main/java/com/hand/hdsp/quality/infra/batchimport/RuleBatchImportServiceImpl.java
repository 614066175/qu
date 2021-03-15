package com.hand.hdsp.quality.infra.batchimport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.domain.repository.RuleRepository;
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
 * @author lgl 2020/12/09 16:41
 * @since 1.0
 */
@Slf4j
@ImportService(templateCode = TemplateCodeConstants.TEMPLATE_CODE_RULE,sheetIndex = 1)
public class RuleBatchImportServiceImpl implements IBatchImportService {
    private final ObjectMapper objectMapper;
    private final RuleRepository ruleRepository;

    public RuleBatchImportServiceImpl(ObjectMapper objectMapper, RuleRepository ruleRepository) {
        this.objectMapper = objectMapper;
        this.ruleRepository = ruleRepository;
    }

    @Override
    public Boolean doImport(List<String> data) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        List<RuleDTO> ruleDTOList = new ArrayList<>(data.size());
        try {
            for (String json : data) {
                RuleDTO ruleDTO = objectMapper.readValue(json, RuleDTO.class);
                ruleDTO.setTenantId(tenantId);
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
