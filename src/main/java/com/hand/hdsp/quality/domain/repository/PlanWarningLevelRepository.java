package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.PlanWarningLevelDTO;
import com.hand.hdsp.quality.domain.entity.PlanWarningLevel;

/**
 * <p>方案告警等级表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface PlanWarningLevelRepository extends BaseRepository<PlanWarningLevel, PlanWarningLevelDTO>, ProxySelf<PlanWarningLevelRepository> {

    /**
     * 删除
     *
     * @param sourceId
     * @param sourceType
     * @return
     */
    int deleteByParentId(Long sourceId, String sourceType);
}
