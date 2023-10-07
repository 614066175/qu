package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.domain.entity.RuleGroup;

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
     * 列表（标准规则租户级）
     *
     * @param ruleGroup 查询条件
     * @return
     */
    List<RuleGroup> list(RuleGroup ruleGroup);
}
