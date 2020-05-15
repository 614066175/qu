package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.domain.entity.Rule;
import com.hand.hdsp.quality.domain.repository.RuleRepository;
import com.hand.hdsp.quality.infra.constant.PlanConstant;
import com.hand.hdsp.quality.infra.mapper.RuleMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

import java.util.Arrays;
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
    public Page<RuleDTO> list2(PageRequest pageRequest, RuleDTO ruleDTO) {
        return PageHelper.doPage(pageRequest, () -> ruleMapper.list2(ruleDTO));
    }

    @Override
    public Page<Rule> listTableRule(RuleDTO ruleDTO, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () ->
                ruleMapper.selectByCondition(Condition.builder(Rule.class)
                        .where(Sqls.custom()
                                .andIn(Rule.FIELD_RULE_TYPE, Arrays.asList(PlanConstant.RuleType.TABLE_TYPE, PlanConstant.RuleType.SQL_CUSTOM))
                                .andEqualTo(Rule.FIELD_ENABLED_FLAG, BaseConstants.Flag.YES)
                                .andLike(Rule.FIELD_RULE_NAME, ruleDTO.getRuleName(), true))
                        .build())
        );
    }
}
