package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.api.dto.RuleGroupDTO;
import com.hand.hdsp.quality.domain.entity.RuleGroup;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * 规则分组表Mapper
 *
 * @author wuzhong26857
 * @version 1.0
 * @date 2020/3/23 13:34
 */
public interface RuleGroupMapper extends BaseMapper<RuleGroup> {
    /**
     * 查询子级分组
     *
     * @param ruleGroupDTO 查询条件
     * @return
     */
    int searchChildGroup(RuleGroupDTO ruleGroupDTO);

    /**
     * 列表（标准规则租户级）
     *
     * @param ruleGroup 查询条件
     * @return
     */
    List<RuleGroup> list(RuleGroup ruleGroup);
}
