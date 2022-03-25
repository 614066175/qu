package com.hand.hdsp.quality.domain.repository;

import java.util.List;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.RuleDTO;
import com.hand.hdsp.quality.domain.entity.Rule;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

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
     * @param tenantId
     * @return
     */
    List<RuleDTO> list(List<String> ruleCodeList, String ruleModel, Long tenantId);

    /**
     * 查询所有
     *
     * @param ruleDTO
     * @return
     */
    List<RuleDTO> listAll(RuleDTO ruleDTO);

    /**
     * 列表(租户级）
     *
     * @param pageRequest
     * @param ruleDTO
     * @return
     */
    Page<RuleDTO> listTenant(PageRequest pageRequest, RuleDTO ruleDTO);

    /**
     * 批量导入
     * @param ruleDTOList
     */
    void batchImport(List<RuleDTO> ruleDTOList);
}
