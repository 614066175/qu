package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.RuleGroupDTO;

/**
 * 规则分组表应用服务
 *
 * @author wuzhong26857
 * @version 1.0
 * @date 2020/3/23 13:25
 */
public interface RuleGroupService {

    /**
     * 删除
     *
     * @param ruleGroupDTO 删除条件
     * @return 删除结果
     */
    int delete(RuleGroupDTO ruleGroupDTO);
}
