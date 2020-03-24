package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.RuleWarningLevel;
import com.hand.hdsp.quality.api.dto.RuleWarningLevelDTO;
import com.hand.hdsp.core.base.ProxySelf;

/**
 * <p>规则告警等级表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
public interface RuleWarningLevelRepository extends BaseRepository<RuleWarningLevel, RuleWarningLevelDTO>, ProxySelf<RuleWarningLevelRepository> {

}