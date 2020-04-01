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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(rollbackFor = Exception.class)
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(RuleDTO ruleDTO) {
        Long tenantId = ruleDTO.getTenantId();
        ruleRepository.updateDTOWhereTenant(ruleDTO, tenantId);
        if (ruleDTO.getRuleLineDTOList() != null) {

            for (RuleLineDTO ruleLineDTO : ruleDTO.getRuleLineDTOList()) {
                ruleLineDTO.setRuleId(ruleDTO.getRuleId());
                ruleLineDTO.setTenantId(tenantId);
                if (ruleLineRepository.selectOne(RuleLine.builder()
                        .ruleLineId(ruleLineDTO.getRuleLineId())
                        .ruleId(ruleLineDTO.getRuleId()).build()) != null) {
                    ruleLineRepository.updateDTOWhereTenant(ruleLineDTO, tenantId);
                } else {
                    ruleLineRepository.insertDTOSelective(ruleLineDTO);
                }

                if (ruleLineDTO.getRuleWarningLevelDTOList() != null) {

                    for (RuleWarningLevelDTO ruleWarningLevelDTO : ruleLineDTO.getRuleWarningLevelDTOList()) {
                        ruleWarningLevelDTO.setRuleLineId(ruleLineDTO.getRuleLineId());
                        ruleWarningLevelDTO.setTenantId(tenantId);
                        if (ruleWarningLevelRepository.selectOne(RuleWarningLevel.builder()
                                .ruleLineId(ruleWarningLevelDTO.getRuleLineId())
                                .levelId(ruleWarningLevelDTO.getLevelId()).build()) != null) {
                            ruleWarningLevelRepository.updateDTOWhereTenant(ruleWarningLevelDTO, tenantId);
                        } else {
                            ruleWarningLevelRepository.insertDTOSelective(ruleWarningLevelDTO);
                        }
                    }
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(RuleDTO ruleDTO) {
        List<RuleLineDTO> ruleLineDTOList = ruleLineRepository.selectDTO(RuleLine.FIELD_RULE_ID, ruleDTO.getRuleId());
        if (ruleLineDTOList != null) {
            for (RuleLineDTO ruleLineDTO : ruleLineDTOList) {
                ruleLineRepository.deleteDTO(ruleLineDTO);
                List<RuleWarningLevelDTO> ruleWarningLevelDTOList =
                        ruleWarningLevelRepository.selectDTO(RuleWarningLevel.FIELD_RULE_LINE_ID, ruleLineDTO.getRuleLineId());
                if (ruleWarningLevelDTOList != null) {
                    for (RuleWarningLevelDTO ruleWarningLevelDTO : ruleWarningLevelDTOList) {
                        ruleWarningLevelRepository.deleteDTO(ruleWarningLevelDTO);
                    }
                }
            }
        }
        return ruleRepository.deleteByPrimaryKey(ruleDTO);
    }
}