package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.NameAimRule;
import com.hand.hdsp.quality.api.dto.NameAimRuleDTO;
import com.hand.hdsp.core.base.ProxySelf;

/**
 * <p>命名标准排除规则表资源库</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
public interface NameAimRuleRepository extends BaseRepository<NameAimRule, NameAimRuleDTO>, ProxySelf<NameAimRuleRepository> {

}