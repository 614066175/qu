package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.RuleDTO;

/**
 * <p>规则表应用服务</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
public interface RuleService {

    /**
     * 关联查询规则校验项、告警等级
     *
     * @param ruleId
     * @return
     */
    RuleDTO detail(Long ruleId);

    /**
     * 同时保存规则校验项、告警等级
     *
     * @param ruleDTO
     */
    void insert(RuleDTO ruleDTO);
}
