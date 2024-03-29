package org.xdsp.quality.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.export.vo.ExportParam;
import org.xdsp.quality.api.dto.RuleDTO;

import java.util.List;

/**
 * <p>规则表应用服务</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
public interface RuleService {

    /**
     * 关联查询规则校验项、告警等级（标准规则租户级）
     *
     * @param ruleId
     * @param tenantId
     * @return
     */
    RuleDTO detail(Long ruleId, Long tenantId,Long projectId);

    /**
     * 同时保存规则校验项、告警等级
     *
     * @param ruleDTO
     */
    void insert(RuleDTO ruleDTO);

    /**
     * 更新规则（含规则校验项、告警等级的更新和创建）
     *
     * @param ruleDTO 更新规则信息
     */
    void update(RuleDTO ruleDTO);

    /**
     * 更新规则平台级（含规则校验项、告警等级的更新和创建）
     *
     * @param ruleDTO 更新规则信息
     */
    void updateSite(RuleDTO ruleDTO);

    /**
     * 删除（含规则校验项、告警等级的删除）
     *
     * @param ruleDTO 删除条件
     * @return 删除结果
     */
    int delete(RuleDTO ruleDTO);

    /**
     * 导出标准规则
     * @param dto
     * @param exportParam
     * @return
     */
    List<RuleDTO> export(RuleDTO dto, ExportParam exportParam);

    /**
     * 分页查询标准规则
     * @param pageRequest 分页参数
     * @param ruleDTO 查询参数
     * @return 标准规则分页结果
     */
    Page<RuleDTO> pageRules(PageRequest pageRequest, RuleDTO ruleDTO);
}
