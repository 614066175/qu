package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.PlanWarningLevelDTO;
import com.hand.hdsp.quality.domain.entity.PlanWarningLevel;
import com.hand.hdsp.quality.domain.repository.PlanWarningLevelRepository;
import com.hand.hdsp.quality.infra.mapper.PlanWarningLevelMapper;
import org.springframework.stereotype.Component;

/**
 * <p>方案告警等级表资源库实现</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Component
public class PlanWarningLevelRepositoryImpl extends BaseRepositoryImpl<PlanWarningLevel, PlanWarningLevelDTO> implements PlanWarningLevelRepository {

    private final PlanWarningLevelMapper planWarningLevelMapper;

    public PlanWarningLevelRepositoryImpl(PlanWarningLevelMapper planWarningLevelMapper) {
        this.planWarningLevelMapper = planWarningLevelMapper;
    }

    @Override
    public int deleteByParentId(Long sourceId, String sourceType) {
        return planWarningLevelMapper.deleteByParentId(sourceId, sourceType);
    }
}
