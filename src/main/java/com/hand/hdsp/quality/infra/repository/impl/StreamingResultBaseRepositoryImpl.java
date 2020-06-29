package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StreamingResultBaseDTO;
import com.hand.hdsp.quality.api.dto.StreamingResultRuleDTO;
import com.hand.hdsp.quality.domain.entity.StreamingResultBase;
import com.hand.hdsp.quality.domain.repository.StreamingResultBaseRepository;
import com.hand.hdsp.quality.infra.mapper.StreamingResultRuleMapper;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.stereotype.Component;

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
                                .andEqualTo(StreamingResultBase.FIELD_TENANT_ID, streamingResultBaseDTO.getTenantId(), true))
                        .build()
        );
        if (!streamingResultBases.isEmpty()){
            streamingResultBases.forEach( s -> s.setResultWaringVOList(streamingResultRuleMapper.selectWarnByTopic(StreamingResultRuleDTO.builder().tenantId(streamingResultBaseDTO.getTenantId()).topicInfo(s.getTopicInfo()).resultBaseId(s.getResultBaseId()).build())));
        }
        return streamingResultBases;
    }
}
