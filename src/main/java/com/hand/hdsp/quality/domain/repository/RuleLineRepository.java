package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.RuleLineDTO;
import com.hand.hdsp.quality.domain.entity.RuleLine;

import java.util.List;

/**
 * <p>规则校验项表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
public interface RuleLineRepository extends BaseRepository<RuleLine, RuleLineDTO>, ProxySelf<RuleLineRepository> {

    /**
     * 删除
     *
     * @param ruleId
     * @return
     */
    int deleteByParentId(Long ruleId);

    /**
     * 列表
     *
     * @param ruleId
     * @param tenantId
     * @return
     */
    List<RuleLineDTO> list(Long ruleId, Long tenantId);
}