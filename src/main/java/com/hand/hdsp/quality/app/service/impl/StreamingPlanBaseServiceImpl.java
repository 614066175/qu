package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.BatchPlanTableDTO;
import com.hand.hdsp.quality.api.dto.StreamingPlanBaseDTO;
import com.hand.hdsp.quality.api.dto.StreamingPlanRuleDTO;
import com.hand.hdsp.quality.app.service.StreamingPlanBaseService;
import com.hand.hdsp.quality.domain.entity.BatchPlanTable;
import com.hand.hdsp.quality.domain.entity.BatchPlanTableLine;
import com.hand.hdsp.quality.domain.entity.StreamingPlanRule;
import com.hand.hdsp.quality.domain.repository.StreamingPlanBaseRepository;
import com.hand.hdsp.quality.domain.repository.StreamingPlanRuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>实时数据方案-基础配置表应用服务默认实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Service
public class StreamingPlanBaseServiceImpl implements StreamingPlanBaseService {

    private final StreamingPlanBaseRepository streamingPlanBaseRepository;
    private final StreamingPlanRuleRepository streamingPlanRuleRepository;

    public StreamingPlanBaseServiceImpl(StreamingPlanBaseRepository streamingPlanBaseRepository, StreamingPlanRuleRepository streamingPlanRuleRepository) {
        this.streamingPlanBaseRepository = streamingPlanBaseRepository;
        this.streamingPlanRuleRepository = streamingPlanRuleRepository;
    }

    @Override
    public int delete(StreamingPlanBaseDTO streamingPlanBaseDTO) {
        streamingPlanRuleRepository.batchDTODelete(
                streamingPlanRuleRepository.selectDTO
                        (StreamingPlanRule.FIELD_PLAN_BASE_ID, streamingPlanBaseDTO.getPlanBaseId()));
        return streamingPlanBaseRepository.deleteByPrimaryKey(streamingPlanBaseDTO);
    }
}
