package org.xdsp.quality.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.RuleLineDTO;
import org.xdsp.quality.domain.entity.RuleLine;

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
    List<RuleLineDTO> list(Long ruleId, Long tenantId ,Long projectId);

    /**
     * 列表（分页）
     *
     * @param pageRequest
     * @param ruleLineDTO
     * @return
     */
    Page<RuleLineDTO> listTenant(PageRequest pageRequest, RuleLineDTO ruleLineDTO);
}
