package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.RuleWarningLevelDTO;
import com.hand.hdsp.quality.domain.entity.RuleWarningLevel;

import java.util.List;

/**
 * <p>规则告警等级表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
public interface RuleWarningLevelRepository extends BaseRepository<RuleWarningLevel, RuleWarningLevelDTO>, ProxySelf<RuleWarningLevelRepository> {

    /**
     * 删除
     *
     * @param ruleLineId
     * @return
     */
    int deleteByParentId(Long ruleLineId);

    /**
     * 列表
     *
     * @param ruleLineId
     * @param tenantId
     * @return
     */
    List<RuleWarningLevelDTO> list(Long ruleLineId, Long tenantId);
}