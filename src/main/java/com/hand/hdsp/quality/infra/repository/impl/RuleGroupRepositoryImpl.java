package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.RuleGroupDTO;
import com.hand.hdsp.quality.domain.entity.RuleGroup;
import com.hand.hdsp.quality.domain.repository.RuleGroupRepository;
import com.hand.hdsp.quality.infra.mapper.RuleGroupMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 规则分组表资源库实现
 *
 * @author wuzhong26857
 * @version 1.0
 * @date 2020/3/23 13:39
 */
@Component
public class RuleGroupRepositoryImpl extends BaseRepositoryImpl<RuleGroup, RuleGroupDTO> implements RuleGroupRepository {

    private final RuleGroupMapper ruleGroupMapper;

    public RuleGroupRepositoryImpl(RuleGroupMapper ruleGroupMapper) {
        this.ruleGroupMapper = ruleGroupMapper;
    }

    @Override
    public List<RuleGroup> list(RuleGroup ruleGroup) {
        return ruleGroupMapper.list(ruleGroup);
    }
}
