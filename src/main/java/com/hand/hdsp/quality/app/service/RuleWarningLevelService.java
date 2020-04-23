package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.RuleWarningLevelDTO;

/**
 * <p>规则告警等级表应用服务</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
public interface RuleWarningLevelService {

    /**
     * 插入，校验告警范围不能重叠
     *
     * @param ruleWarningLevelDTO
     * @return
     */
    int insertAndValid(RuleWarningLevelDTO ruleWarningLevelDTO);

    /**
     * 更新，校验告警范围不能重叠
     *
     * @param ruleWarningLevelDTO
     * @param tenantId
     * @return
     */
    int updateAndValid(RuleWarningLevelDTO ruleWarningLevelDTO, Long tenantId);
}
