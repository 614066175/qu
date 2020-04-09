package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.domain.entity.Rule;

import java.util.List;

/**
 * <p>规则表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
public interface RuleRepository extends BaseRepository<Rule, RuleDTO>, ProxySelf<RuleRepository> {

    /**
     * 列表
     *
     * @param ruleCodeList
     * @param ruleModel
     * @return
     */
    List<RuleDTO> list(List<String> ruleCodeList, String ruleModel);

    /**
     * 查询所有
     *
     * @param ruleDTO
     * @return
     */
    List<RuleDTO> listAll(RuleDTO ruleDTO);
}
