package org.xdsp.quality.infra.repository.impl;

import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.StreamingResultBaseDTO;
import org.xdsp.quality.api.dto.StreamingResultRuleDTO;
import org.xdsp.quality.domain.entity.StreamingResultBase;
import org.xdsp.quality.domain.repository.StreamingResultBaseRepository;
import org.xdsp.quality.infra.mapper.StreamingResultRuleMapper;

import java.util.List;

/**
 * <p>实时数据方案结果表-基础信息资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
@Component
public class StreamingResultBaseRepositoryImpl extends BaseRepositoryImpl<StreamingResultBase, StreamingResultBaseDTO> implements StreamingResultBaseRepository {

    private final StreamingResultRuleMapper streamingResultRuleMapper;

    public StreamingResultBaseRepositoryImpl(StreamingResultRuleMapper streamingResultRuleMapper) {
        this.streamingResultRuleMapper = streamingResultRuleMapper;
    }

    @Override
    public List<StreamingResultBaseDTO> listResultBase(StreamingResultBaseDTO streamingResultBaseDTO) {
        List<StreamingResultBaseDTO> streamingResultBases = this.selectDTOByCondition(
                Condition.builder(StreamingResultBase.class)
                        .where(Sqls.custom()
                                .andEqualTo(StreamingResultBase.FIELD_RESULT_ID, streamingResultBaseDTO.getResultId(), true)
                                .andEqualTo(StreamingResultBase.FIELD_PROJECT_ID, streamingResultBaseDTO.getProjectId(),true)
                                .andEqualTo(StreamingResultBase.FIELD_TENANT_ID, streamingResultBaseDTO.getTenantId(), true))
                        .build()
        );
        if (!streamingResultBases.isEmpty()){
            streamingResultBases.forEach( s -> s.setResultWaringVOList(streamingResultRuleMapper.selectWarnByTopic(StreamingResultRuleDTO.builder().tenantId(streamingResultBaseDTO.getTenantId()).projectId(streamingResultBaseDTO.getProjectId()).topicInfo(s.getTopicInfo()).resultBaseId(s.getResultBaseId()).build())));
        }
        return streamingResultBases;
    }
}
