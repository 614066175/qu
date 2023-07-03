package org.xdsp.quality.app.service;

import org.hzero.export.vo.ExportParam;
import org.xdsp.quality.api.dto.RuleDTO;
import org.xdsp.quality.api.dto.RuleGroupDTO;
import org.xdsp.quality.domain.entity.RuleGroup;
import org.xdsp.quality.infra.export.dto.StandardRuleExportDTO;

import java.util.List;

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

    /**
     * 从分组开始导出标准规则
     * @param dto 导出的查询条件
     * @param exportParam 导出参数
     * @return 导出的标准规则
     */
    List<StandardRuleExportDTO> export(RuleDTO dto, ExportParam exportParam);

    /**
     * 新建分组
     * @param dto
     * @return
     */
    int create(RuleGroupDTO dto);
}
