package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.PlanWarningLevelDTO;
import com.hand.hdsp.quality.api.dto.StreamingPlanBaseDTO;
import com.hand.hdsp.quality.api.dto.StreamingPlanRuleDTO;
import com.hand.hdsp.quality.app.service.StreamingPlanBaseService;
import com.hand.hdsp.quality.domain.entity.PlanWarningLevel;
import com.hand.hdsp.quality.domain.entity.StreamingPlanRule;
import com.hand.hdsp.quality.domain.repository.PlanWarningLevelRepository;
import com.hand.hdsp.quality.domain.repository.StreamingPlanBaseRepository;
import com.hand.hdsp.quality.domain.repository.StreamingPlanRuleRepository;
import com.hand.hdsp.quality.infra.constant.TableNameConstant;
import com.hand.hdsp.quality.infra.converter.PlanWarningLevelConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final PlanWarningLevelRepository planWarningLevelRepository;
    private final PlanWarningLevelConverter planWarningLevelConverter;

    public StreamingPlanBaseServiceImpl(StreamingPlanBaseRepository streamingPlanBaseRepository, StreamingPlanRuleRepository streamingPlanRuleRepository, PlanWarningLevelRepository planWarningLevelRepository, PlanWarningLevelConverter planWarningLevelConverter) {
        this.streamingPlanBaseRepository = streamingPlanBaseRepository;
        this.streamingPlanRuleRepository = streamingPlanRuleRepository;
        this.planWarningLevelRepository = planWarningLevelRepository;
        this.planWarningLevelConverter = planWarningLevelConverter;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(StreamingPlanBaseDTO streamingPlanBaseDTO) {
        List<StreamingPlanRuleDTO> streamingPlanRuleDTOList = streamingPlanRuleRepository.selectDTO(
                StreamingPlanRule.FIELD_PLAN_BASE_ID, streamingPlanBaseDTO);
        if (streamingPlanRuleDTOList != null) {
            for (StreamingPlanRuleDTO streamingPlanRuleDTO : streamingPlanRuleDTOList) {
                planWarningLevelRepository.deleteByParentId(streamingPlanRuleDTO.getPlanRuleId(),
                        TableNameConstant.XQUA_STREAMING_PLAN_RULE);
            }
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
                streamingPlanRuleDTO.setPlanBaseId(streamingPlanBaseDTO.getPlanBaseId());
                streamingPlanRuleDTO.setTenantId(tenantId);
                if (streamingPlanRuleRepository.selectOne(StreamingPlanRule.builder()
                        .planRuleId(streamingPlanRuleDTO.getPlanRuleId())
                        .planBaseId(streamingPlanRuleDTO.getPlanBaseId()).build()) != null) {
                    streamingPlanRuleRepository.updateDTOWhereTenant(streamingPlanRuleDTO, tenantId);
                } else {
                    streamingPlanRuleRepository.insertDTOSelective(streamingPlanRuleDTO);
                }
                if (streamingPlanRuleDTO.getPlanWarningLevelDTOList() != null) {
                    for (PlanWarningLevelDTO planWarningLevelDTO : streamingPlanRuleDTO.getPlanWarningLevelDTOList()) {
                        planWarningLevelDTO.setSourceId(streamingPlanRuleDTO.getPlanRuleId());
                        planWarningLevelDTO.setSourceType(TableNameConstant.XQUA_STREAMING_PLAN_RULE);
                        planWarningLevelDTO.setTenantId(tenantId);
                        if (planWarningLevelRepository.selectOne(PlanWarningLevel.builder()
                                .sourceId(planWarningLevelDTO.getSourceId())
                                .sourceType(planWarningLevelDTO.getSourceType()).build()) != null) {
                            planWarningLevelRepository.updateDTOWhereTenant(planWarningLevelDTO, tenantId);
                        }
                        planWarningLevelRepository.insertDTOSelective(planWarningLevelDTO);
                    }
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
                if (streamingPlanRuleDTO.getPlanWarningLevelDTOList() != null) {
                    for (PlanWarningLevelDTO planWarningLevelDTO : streamingPlanRuleDTO.getPlanWarningLevelDTOList()) {
                        planWarningLevelDTO.setSourceId(streamingPlanRuleDTO.getPlanRuleId());
                        planWarningLevelDTO.setSourceType(TableNameConstant.XQUA_STREAMING_PLAN_RULE);
                        planWarningLevelDTO.setTenantId(tenantId);
                        planWarningLevelRepository.insertDTOSelective(planWarningLevelDTO);
                    }
                }
            }
        }
    }

    @Override
    public StreamingPlanBaseDTO detail(Long planBaseId) {
        StreamingPlanBaseDTO streamingPlanBaseDTO = streamingPlanBaseRepository.selectDTOByPrimaryKey(planBaseId);
        List<StreamingPlanRuleDTO> streamingPlanRuleDTOList = streamingPlanRuleRepository.selectDTO(StreamingPlanRule.FIELD_PLAN_BASE_ID, planBaseId);
        for (StreamingPlanRuleDTO streamingPlanRuleDTO : streamingPlanRuleDTOList) {
            List<PlanWarningLevel> planWarningLevelList = planWarningLevelRepository.select(
                    PlanWarningLevel.builder()
                            .sourceId(streamingPlanRuleDTO.getPlanRuleId())
                            .sourceType(TableNameConstant.XQUA_STREAMING_PLAN_RULE).build());
            List<PlanWarningLevelDTO> planWarningLevelDTOList = new ArrayList<>();
            for (PlanWarningLevel planWarningLevel : planWarningLevelList) {
                planWarningLevelDTOList.add(planWarningLevelConverter.entityToDto(planWarningLevel));
            }
            streamingPlanRuleDTO.setPlanWarningLevelDTOList(planWarningLevelDTOList);
        }
        streamingPlanBaseDTO.setStreamingPlanRuleDTOList(streamingPlanRuleDTOList);
        return streamingPlanBaseDTO;
    }
}
