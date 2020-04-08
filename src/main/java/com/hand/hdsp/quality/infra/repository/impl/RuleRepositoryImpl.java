package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.domain.entity.Rule;
import com.hand.hdsp.quality.domain.repository.RuleRepository;
import com.hand.hdsp.quality.infra.mapper.RuleMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>规则表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
@Component
public class RuleRepositoryImpl extends BaseRepositoryImpl<Rule, RuleDTO> implements RuleRepository {

    private final RuleMapper ruleMapper;

    public RuleRepositoryImpl(RuleMapper ruleMapper) {
        this.ruleMapper = ruleMapper;
    }

    @Override
    public List<RuleDTO> list(List<String> ruleCodeList, String ruleModel) {
        return ruleMapper.list(ruleCodeList, ruleModel);
    }

    @Override
    public List<RuleDTO> listAll(String ruleModel) {
        return ruleMapper.listAll(ruleModel);
    }
}
