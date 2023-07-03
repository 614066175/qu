package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.RuleGroupDTO;
import org.xdsp.quality.domain.entity.RuleGroup;

import java.util.List;

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
}
