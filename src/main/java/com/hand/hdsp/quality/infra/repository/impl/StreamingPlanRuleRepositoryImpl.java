package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StreamingPlanRuleDTO;
import com.hand.hdsp.quality.domain.entity.StreamingPlanRule;
import com.hand.hdsp.quality.domain.repository.StreamingPlanRuleRepository;
import com.hand.hdsp.quality.infra.mapper.StreamingPlanRuleMapper;
import org.springframework.stereotype.Component;

/**
 * <p>实时数据方案-规则表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Component
public class StreamingPlanRuleRepositoryImpl extends BaseRepositoryImpl<StreamingPlanRule, StreamingPlanRuleDTO> implements StreamingPlanRuleRepository {

    private final StreamingPlanRuleMapper streamingPlanRuleMapper;

    public StreamingPlanRuleRepositoryImpl(StreamingPlanRuleMapper streamingPlanRuleMapper) {
        this.streamingPlanRuleMapper = streamingPlanRuleMapper;
    }

    @Override
    public int deleteByParentId(Long planBaseId) {
        return streamingPlanRuleMapper.deleteByParentId(planBaseId);
    }
}
