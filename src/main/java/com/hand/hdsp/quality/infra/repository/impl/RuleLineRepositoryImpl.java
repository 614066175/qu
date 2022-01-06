package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.RuleLineDTO;
import com.hand.hdsp.quality.domain.entity.RuleLine;
import com.hand.hdsp.quality.domain.repository.RuleLineRepository;
import com.hand.hdsp.quality.infra.mapper.RuleLineMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>规则校验项表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
@Component
public class RuleLineRepositoryImpl extends BaseRepositoryImpl<RuleLine, RuleLineDTO> implements RuleLineRepository {

    private final RuleLineMapper ruleLineMapper;

    public RuleLineRepositoryImpl(RuleLineMapper ruleLineMapper) {
        this.ruleLineMapper = ruleLineMapper;
    }

    @Override
    public int deleteByParentId(Long ruleId) {
        return ruleLineMapper.deleteByParentId(ruleId);
    }

    @Override
    public List<RuleLineDTO> list(Long ruleId, Long tenantId,Long projectId) {
        return ruleLineMapper.list(ruleId, tenantId,projectId);
    }

    @Override
    public Page<RuleLineDTO> listTenant(PageRequest pageRequest, RuleLineDTO ruleLineDTO) {
        return PageHelper.doPage(pageRequest, () -> ruleLineMapper.listTenant(ruleLineDTO));
    }
}
