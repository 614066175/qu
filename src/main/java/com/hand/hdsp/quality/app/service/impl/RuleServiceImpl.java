package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.api.dto.RuleLineDTO;
import com.hand.hdsp.quality.api.dto.RuleWarningLevelDTO;
import com.hand.hdsp.quality.app.service.RuleService;
import com.hand.hdsp.quality.domain.entity.RuleLine;
import com.hand.hdsp.quality.domain.entity.RuleWarningLevel;
import com.hand.hdsp.quality.domain.repository.RuleLineRepository;
import com.hand.hdsp.quality.domain.repository.RuleRepository;
import com.hand.hdsp.quality.domain.repository.RuleWarningLevelRepository;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>规则表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
@Service
public class RuleServiceImpl implements RuleService {

    private final RuleRepository ruleRepository;
    private final RuleLineRepository ruleLineRepository;
    private final RuleWarningLevelRepository ruleWarningLevelRepository;

    public RuleServiceImpl(RuleRepository ruleRepository, RuleLineRepository ruleLineRepository, RuleWarningLevelRepository ruleWarningLevelRepository) {
        this.ruleRepository = ruleRepository;
        this.ruleLineRepository = ruleLineRepository;
        this.ruleWarningLevelRepository = ruleWarningLevelRepository;
    }

    @Override
    public RuleDTO detail(Long ruleId) {
        RuleDTO ruleDTO = ruleRepository.selectDTOByPrimaryKey(ruleId);
        List<RuleLineDTO> ruleLineDTOList = ruleLineRepository.selectDTO(RuleLine.FIELD_RULE_ID, ruleId);
        for (RuleLineDTO ruleLineDTO : ruleLineDTOList) {
            ruleLineDTO.setRuleWarningLevelDTOList(ruleWarningLevelRepository.selectDTO(RuleWarningLevel.FIELD_RULE_LINE_ID, ruleLineDTO.getRuleLineId()));
        }
        ruleDTO.setRuleLineDTOList(ruleLineDTOList);
        return ruleDTO;
    }

    @Override
    public void insert(RuleDTO ruleDTO) {
        Long tenantId = ruleDTO.getTenantId();
        ruleRepository.insertDTOSelective(ruleDTO);
        if (ruleDTO.getRuleLineDTOList() != null) {

            for (RuleLineDTO ruleLineDTO : ruleDTO.getRuleLineDTOList()) {
                ruleLineDTO.setRuleId(ruleDTO.getRuleId());
                ruleLineDTO.setTenantId(tenantId);
                ruleLineRepository.insertDTOSelective(ruleLineDTO);

                if (ruleLineDTO.getRuleWarningLevelDTOList() != null) {
                    for (RuleWarningLevelDTO ruleWarningLevelDTO : ruleLineDTO.getRuleWarningLevelDTOList()) {
                        ruleWarningLevelDTO.setRuleLineId(ruleLineDTO.getRuleLineId());
                        ruleWarningLevelDTO.setTenantId(tenantId);
                        ruleWarningLevelRepository.insertDTOSelective(ruleWarningLevelDTO);
                    }
                }
            }
        }
    }
}
