package com.hand.hdsp.quality.domain.repository;

import java.util.List;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.api.dto.RuleGroupDTO;
import com.hand.hdsp.quality.domain.entity.RuleGroup;

/**
 * 规则分组表资源库
 *
 * @author wuzhong26857
 * @version 1.0
 * @date 2020/3/23 13:29
 */
public interface RuleGroupRepository extends BaseRepository<RuleGroup, RuleGroupDTO>, ProxySelf<RuleGroupRepository> {

    /**
     * 根据方案名查到所在分组
     *
     * @param ruleGroup
     * @return List<RuleGroup>
     */
    List<RuleGroup> list(RuleGroup ruleGroup);

    /**
     * 批量导入
     * @param ruleGroupDTOList
     */
    void batchImport(List<RuleGroupDTO> ruleGroupDTOList);

    /**
     * 查询子级分组
     * @param ruleGroupDTO
     * @return boolean
     */
    boolean searchChildGroup(RuleGroupDTO ruleGroupDTO);
}
