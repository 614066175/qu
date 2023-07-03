package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.NameAimRuleDTO;
import org.xdsp.quality.domain.entity.NameAimRule;

/**
 * <p>命名标准排除规则表资源库</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
public interface NameAimRuleRepository extends BaseRepository<NameAimRule, NameAimRuleDTO>, ProxySelf<NameAimRuleRepository> {

}