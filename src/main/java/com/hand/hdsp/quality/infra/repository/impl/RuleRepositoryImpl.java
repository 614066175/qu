package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.domain.entity.Rule;
import com.hand.hdsp.quality.domain.repository.RuleRepository;
import com.hand.hdsp.quality.infra.mapper.RuleMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
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
    public List<RuleDTO> list(List<String> ruleCodeList, String ruleModel, Long tenantId) {
        return ruleMapper.list(ruleCodeList, ruleModel, tenantId);
    }

    @Override
    public List<RuleDTO> listAll(RuleDTO ruleDTO) {
        return ruleMapper.listAll(ruleDTO);
    }

    @Override
    public Page<RuleDTO> listTenant(PageRequest pageRequest, RuleDTO ruleDTO) {
        return PageHelper.doPage(pageRequest, () -> ruleMapper.listTenant(ruleDTO));
    }

}
