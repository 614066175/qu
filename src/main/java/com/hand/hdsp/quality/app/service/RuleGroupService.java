package com.hand.hdsp.quality.app.service;

import java.util.List;

import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.api.dto.RuleGroupDTO;
import com.hand.hdsp.quality.domain.entity.RuleGroup;
import org.hzero.export.vo.ExportParam;

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

    /**
     * 查询，处理所有分组逻辑
     *
     * @param ruleGroup
     * @return
     */
    List<RuleGroup> selectList(RuleGroup ruleGroup);

    /**
     * 查询，处理所有分组逻辑（标准规则租户级）
     *
     * @param ruleGroup
     * @return
     */
    List<RuleGroup> listNoPage(RuleGroup ruleGroup);

    List<RuleGroupDTO> export(RuleDTO dto, ExportParam exportParam);
}
