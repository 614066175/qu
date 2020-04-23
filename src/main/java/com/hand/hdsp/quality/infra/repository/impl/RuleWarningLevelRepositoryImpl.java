package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.RuleWarningLevelDTO;
import com.hand.hdsp.quality.domain.entity.RuleWarningLevel;
import com.hand.hdsp.quality.domain.repository.RuleWarningLevelRepository;
import com.hand.hdsp.quality.infra.mapper.RuleWarningLevelMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>规则告警等级表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
@Component
public class RuleWarningLevelRepositoryImpl extends BaseRepositoryImpl<RuleWarningLevel, RuleWarningLevelDTO> implements RuleWarningLevelRepository {

    private final RuleWarningLevelMapper ruleWarningLevelMapper;

    public RuleWarningLevelRepositoryImpl(RuleWarningLevelMapper ruleWarningLevelMapper) {
        this.ruleWarningLevelMapper = ruleWarningLevelMapper;
    }

    @Override
    public int deleteByParentId(Long ruleLineId) {
        return ruleWarningLevelMapper.deleteByParentId(ruleLineId);
    }

    @Override
    public List<RuleWarningLevelDTO> list(Long ruleLineId, Long tenantId) {
        return ruleWarningLevelMapper.list(ruleLineId, tenantId);
    }

    @Override
    public Page<RuleWarningLevelDTO> list2(PageRequest pageRequest, RuleWarningLevelDTO ruleWarningLevelDTO) {
        return PageHelper.doPage(pageRequest, () -> ruleWarningLevelMapper.list2(ruleWarningLevelDTO));
    }

    @Override
    public int judgeOverlap(RuleWarningLevelDTO ruleWarningLevelDTO) {
        return ruleWarningLevelMapper.judgeOverlap(ruleWarningLevelDTO);
    }
}
