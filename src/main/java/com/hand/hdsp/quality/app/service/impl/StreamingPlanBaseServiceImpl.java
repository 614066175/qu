package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.StreamingPlanBaseDTO;
import com.hand.hdsp.quality.api.dto.StreamingPlanRuleDTO;
import com.hand.hdsp.quality.app.service.StreamingPlanBaseService;
import com.hand.hdsp.quality.domain.entity.StreamingPlanRule;
import com.hand.hdsp.quality.domain.repository.StreamingPlanBaseRepository;
import com.hand.hdsp.quality.domain.repository.StreamingPlanRuleRepository;
import org.springframework.stereotype.Service;

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
        if (streamingPlanBaseDTO.getStreamingPlanRuleDTOList() != null) {
            for (StreamingPlanRuleDTO streamingPlanRuleDTO : streamingPlanBaseDTO.getStreamingPlanRuleDTOList()) {
                streamingPlanRuleRepository.deleteDTO(streamingPlanRuleDTO);
            }
        }
        return streamingPlanBaseRepository.deleteByPrimaryKey(streamingPlanBaseDTO);
    }

    @Override
    public void update(StreamingPlanBaseDTO streamingPlanBaseDTO) {
        Long tenantId = streamingPlanBaseDTO.getTenantId();
        streamingPlanBaseRepository.updateDTOWhereTenant(streamingPlanBaseDTO, tenantId);
        if (streamingPlanBaseDTO.getStreamingPlanRuleDTOList() != null) {
            for (StreamingPlanRuleDTO streamingPlanRuleDTO : streamingPlanBaseDTO.getStreamingPlanRuleDTOList()) {
                streamingPlanRuleDTO.setPlanBaseId(streamingPlanBaseDTO.getPlanBaseId());
                streamingPlanRuleDTO.setTenantId(tenantId);
                if (streamingPlanRuleRepository.selectOne(StreamingPlanRule.builder()
                        .planRuleId(streamingPlanRuleDTO.getPlanRuleId())
                        .planBaseId(streamingPlanRuleDTO.getPlanBaseId()).build()) != null) {
                    streamingPlanRuleRepository.updateDTOWhereTenant(streamingPlanRuleDTO, tenantId);
                } else {
                    streamingPlanRuleRepository.insertDTOSelective(streamingPlanRuleDTO);
                }
            }
        }
    }

    @Override
    public void insert(StreamingPlanBaseDTO streamingPlanBaseDTO) {
        Long tenantId = streamingPlanBaseDTO.getTenantId();
        streamingPlanBaseRepository.insertDTOSelective(streamingPlanBaseDTO);
        if (streamingPlanBaseDTO.getStreamingPlanRuleDTOList() != null) {
            for (StreamingPlanRuleDTO streamingPlanRuleDTO : streamingPlanBaseDTO.getStreamingPlanRuleDTOList()) {
                streamingPlanRuleDTO.setPlanBaseId(streamingPlanBaseDTO.getPlanBaseId());
                streamingPlanRuleDTO.setTenantId(tenantId);
                streamingPlanRuleRepository.insertDTOSelective(streamingPlanRuleDTO);
            }
        }
    }

    @Override
    public StreamingPlanBaseDTO detail(Long planBaseId) {
        StreamingPlanBaseDTO streamingPlanBaseDTO = streamingPlanBaseRepository.selectDTOByPrimaryKey(planBaseId);
        streamingPlanBaseDTO.setStreamingPlanRuleDTOList(streamingPlanRuleRepository.selectDTO(StreamingPlanRule.FIELD_PLAN_BASE_ID, planBaseId));
        return streamingPlanBaseDTO;
    }
}
