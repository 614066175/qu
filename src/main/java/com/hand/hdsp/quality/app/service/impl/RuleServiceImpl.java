package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.api.dto.RuleLineDTO;
import com.hand.hdsp.quality.app.service.RuleService;
import com.hand.hdsp.quality.domain.entity.RuleLine;
import com.hand.hdsp.quality.domain.repository.RuleLineRepository;
import com.hand.hdsp.quality.domain.repository.RuleRepository;
import com.hand.hdsp.quality.infra.util.JsonUtils;
import io.choerodon.mybatis.domain.AuditDomain;
import org.apache.commons.collections4.CollectionUtils;
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

    public RuleServiceImpl(RuleRepository ruleRepository, RuleLineRepository ruleLineRepository) {
        this.ruleRepository = ruleRepository;
        this.ruleLineRepository = ruleLineRepository;
    }


    @Override
    public RuleDTO detail(Long ruleId, Long tenantId) {
        RuleDTO ruleDTO = ruleRepository.selectDTOByPrimaryKey(ruleId);
        List<RuleLineDTO> ruleLineDTOList = ruleLineRepository.list(ruleId, tenantId);
        for (RuleLineDTO ruleLineDTO : ruleLineDTOList) {
            ruleLineDTO.setWarningLevelList(JsonUtils.json2WarningLevel(ruleLineDTO.getWarningLevel()));
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
                //todo 范围重叠判断
                ruleLineDTO.setWarningLevel(JsonUtils.object2Json(ruleLineDTO.getWarningLevelList()));
                ruleLineRepository.insertDTOSelective(ruleLineDTO);
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
                if (AuditDomain.RecordStatus.update.equals(ruleLineDTO.get_status())) {
                    //todo 范围重叠判断
                    ruleLineDTO.setWarningLevel(JsonUtils.object2Json(ruleLineDTO.getWarningLevelList()));
                    ruleLineRepository.updateDTOWhereTenant(ruleLineDTO, tenantId);
                } else if (AuditDomain.RecordStatus.create.equals(ruleLineDTO.get_status())) {
                    ruleLineDTO.setRuleId(ruleDTO.getRuleId());
                    ruleLineDTO.setTenantId(tenantId);
                    //todo 范围重叠判断
                    ruleLineDTO.setWarningLevel(JsonUtils.object2Json(ruleLineDTO.getWarningLevelList()));
                    ruleLineRepository.insertDTOSelective(ruleLineDTO);
                } else if (AuditDomain.RecordStatus.delete.equals(ruleLineDTO.get_status())) {
                    ruleLineRepository.deleteByPrimaryKey(ruleLineDTO);
                }

            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSite(RuleDTO ruleDTO) {
        ruleRepository.updateByDTOPrimaryKeySelective(ruleDTO);
        if (ruleDTO.getRuleLineDTOList() != null) {
            for (RuleLineDTO ruleLineDTO : ruleDTO.getRuleLineDTOList()) {
                if (AuditDomain.RecordStatus.update.equals(ruleLineDTO.get_status())) {
                    //todo 范围重叠判断
                    ruleLineDTO.setWarningLevel(JsonUtils.object2Json(ruleLineDTO.getWarningLevelList()));
                    ruleLineRepository.updateByDTOPrimaryKeySelective(ruleLineDTO);
                } else if (AuditDomain.RecordStatus.create.equals(ruleLineDTO.get_status())) {
                    ruleLineDTO.setRuleId(ruleDTO.getRuleId());
                    ruleLineDTO.setTenantId(0L);
                    //todo 范围重叠判断
                    ruleLineDTO.setWarningLevel(JsonUtils.object2Json(ruleLineDTO.getWarningLevelList()));
                    ruleLineRepository.insertDTOSelective(ruleLineDTO);
                } else if (AuditDomain.RecordStatus.delete.equals(ruleLineDTO.get_status())) {
                    ruleLineRepository.deleteByPrimaryKey(ruleLineDTO);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(RuleDTO ruleDTO) {
        List<RuleLineDTO> ruleLineDTOList = ruleLineRepository.selectDTO(RuleLine.FIELD_RULE_ID, ruleDTO.getRuleId());
        if (CollectionUtils.isNotEmpty(ruleLineDTOList )) {
            ruleLineRepository.deleteByParentId(ruleDTO.getRuleId());
        }
        return ruleRepository.deleteByPrimaryKey(ruleDTO);
    }
}
