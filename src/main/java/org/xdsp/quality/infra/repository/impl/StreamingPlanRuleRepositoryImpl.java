package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.StreamingPlanRuleDTO;
import org.xdsp.quality.domain.entity.StreamingPlanRule;
import org.xdsp.quality.domain.repository.StreamingPlanRuleRepository;
import org.xdsp.quality.infra.mapper.StreamingPlanRuleMapper;

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
