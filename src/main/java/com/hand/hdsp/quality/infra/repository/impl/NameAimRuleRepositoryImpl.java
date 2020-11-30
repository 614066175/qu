package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.NameAimRule;
import com.hand.hdsp.quality.api.dto.NameAimRuleDTO;
import com.hand.hdsp.quality.domain.repository.NameAimRuleRepository;
import org.springframework.stereotype.Component;

/**
 * <p>命名标准排除规则表资源库实现</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Component
public class NameAimRuleRepositoryImpl extends BaseRepositoryImpl<NameAimRule, NameAimRuleDTO> implements NameAimRuleRepository {

}
