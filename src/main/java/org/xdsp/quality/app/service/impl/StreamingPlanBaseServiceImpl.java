package org.xdsp.quality.app.service.impl;

import io.choerodon.mybatis.domain.AuditDomain;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xdsp.quality.api.dto.StreamingPlanBaseDTO;
import org.xdsp.quality.api.dto.StreamingPlanRuleDTO;
import org.xdsp.quality.app.service.StreamingPlanBaseService;
import org.xdsp.quality.domain.entity.StreamingPlanRule;
import org.xdsp.quality.domain.repository.StreamingPlanBaseRepository;
import org.xdsp.quality.domain.repository.StreamingPlanRuleRepository;

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
        if (CollectionUtils.isNotEmpty(streamingPlanRuleDTOList)) {
            streamingPlanRuleRepository.deleteByParentId(streamingPlanBaseDTO.getPlanBaseId());
        }
        return streamingPlanBaseRepository.deleteByPrimaryKey(streamingPlanBaseDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StreamingPlanBaseDTO streamingPlanBaseDTO) {
        Long tenantId = streamingPlanBaseDTO.getTenantId();
        Long projectId = streamingPlanBaseDTO.getProjectId();
        streamingPlanBaseRepository.updateDTOAllColumnWhereTenant(streamingPlanBaseDTO, tenantId);
        if (streamingPlanBaseDTO.getStreamingPlanRuleDTOList() != null) {
            for (StreamingPlanRuleDTO streamingPlanRuleDTO : streamingPlanBaseDTO.getStreamingPlanRuleDTOList()) {
                if (AuditDomain.RecordStatus.update.equals(streamingPlanRuleDTO.get_status())) {
                    streamingPlanRuleDTO.setProjectId(projectId);
                    streamingPlanRuleRepository.updateDTOAllColumnWhereTenant(streamingPlanRuleDTO, tenantId);
                } else if (AuditDomain.RecordStatus.create.equals(streamingPlanRuleDTO.get_status())) {
                    streamingPlanRuleDTO.setPlanBaseId(streamingPlanRuleDTO.getPlanBaseId());
                    streamingPlanRuleDTO.setTenantId(tenantId);
                    streamingPlanRuleDTO.setProjectId(projectId);
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
                streamingPlanBaseDTO.setProjectId(streamingPlanBaseDTO.getProjectId());
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
