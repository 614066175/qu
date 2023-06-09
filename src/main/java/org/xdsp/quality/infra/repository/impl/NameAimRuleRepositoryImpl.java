package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.NameAimRuleDTO;
import org.xdsp.quality.domain.entity.NameAimRule;
import org.xdsp.quality.domain.repository.NameAimRuleRepository;

/**
 * <p>命名标准排除规则表资源库实现</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Component
public class NameAimRuleRepositoryImpl extends BaseRepositoryImpl<NameAimRule, NameAimRuleDTO> implements NameAimRuleRepository {

}
