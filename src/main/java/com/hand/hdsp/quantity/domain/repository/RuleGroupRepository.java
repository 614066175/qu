package com.hand.hdsp.quantity.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quantity.api.dto.RuleGroupDTO;
import com.hand.hdsp.quantity.domain.entity.RuleGroup;

/**
 * 规则分组表资源库
 *
 * @author wuzhong26857
 * @version 1.0
 * @date 2020/3/23 13:29
 */
public interface RuleGroupRepository extends BaseRepository<RuleGroup, RuleGroupDTO>, ProxySelf<RuleGroupRepository> {
}
