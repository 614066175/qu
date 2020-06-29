package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.StreamingPlanBaseDTO;
import com.hand.hdsp.quality.api.dto.StreamingPlanRuleDTO;
import com.hand.hdsp.quality.app.service.StreamingPlanBaseService;
import com.hand.hdsp.quality.domain.entity.StreamingPlanRule;
import com.hand.hdsp.quality.domain.repository.StreamingPlanBaseRepository;
import com.hand.hdsp.quality.domain.repository.StreamingPlanRuleRepository;
import io.choerodon.mybatis.domain.AuditDomain;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(rollbackFor = Exception.class)
    public int delete(StreamingPlanBaseDTO streamingPlanBaseDTO) {
        List<StreamingPlanRuleDTO> streamingPlanRuleDTOList = streamingPlanRuleRepository.selectDTO(
                StreamingPlanRule.FIELD_PLAN_BASE_ID, streamingPlanBaseDTO);
        if (CollectionUtils.isNotEmpty(streamingPlanRuleDTOList )) {
            streamingPlanRuleRepository.deleteByParentId(streamingPlanBaseDTO.getPlanBaseId());
        }
        return streamingPlanBaseRepository.deleteByPrimaryKey(streamingPlanBaseDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StreamingPlanBaseDTO streamingPlanBaseDTO) {
        Long tenantId = streamingPlanBaseDTO.getTenantId();
        streamingPlanBaseRepository.updateDTOWhereTenant(streamingPlanBaseDTO, tenantId);
        if (streamingPlanBaseDTO.getStreamingPlanRuleDTOList() != null) {
            for (StreamingPlanRuleDTO streamingPlanRuleDTO : streamingPlanBaseDTO.getStreamingPlanRuleDTOList()) {
                if (AuditDomain.RecordStatus.update.equals(streamingPlanRuleDTO.get_status())) {
                    streamingPlanRuleRepository.updateDTOWhereTenant(streamingPlanRuleDTO, tenantId);
                } else if (AuditDomain.RecordStatus.create.equals(streamingPlanRuleDTO.get_status())) {
                    streamingPlanRuleDTO.setPlanBaseId(streamingPlanRuleDTO.getPlanBaseId());
                    streamingPlanRuleDTO.setTenantId(tenantId);
                    streamingPlanRuleRepository.insertDTOSelective(streamingPlanRuleDTO);
                } else if (AuditDomain.RecordStatus.delete.equals(streamingPlanRuleDTO.get_status())) {
                    streamingPlanRuleRepository.deleteByPrimaryKey(streamingPlanRuleDTO);
                }

            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
        List<StreamingPlanRuleDTO> streamingPlanRuleDTOList = streamingPlanRuleRepository.selectDTO(StreamingPlanRule.FIELD_PLAN_BASE_ID, planBaseId);

        streamingPlanBaseDTO.setStreamingPlanRuleDTOList(streamingPlanRuleDTOList);
        return streamingPlanBaseDTO;
    }
}
