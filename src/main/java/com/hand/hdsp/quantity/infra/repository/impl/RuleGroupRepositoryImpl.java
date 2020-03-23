package com.hand.hdsp.quantity.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quantity.api.dto.RuleGroupDTO;
import com.hand.hdsp.quantity.domain.entity.RuleGroup;
import com.hand.hdsp.quantity.domain.repository.RuleGroupRepository;
import org.springframework.stereotype.Component;

/**
 * 规则分组表资源库实现
 *
 * @author wuzhong26857
 * @version 1.0
 * @date 2020/3/23 13:39
 */
@Component
public class RuleGroupRepositoryImpl extends BaseRepositoryImpl<RuleGroup, RuleGroupDTO> implements RuleGroupRepository {
}
