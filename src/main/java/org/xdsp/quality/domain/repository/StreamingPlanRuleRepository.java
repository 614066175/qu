package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.StreamingPlanRuleDTO;
import org.xdsp.quality.domain.entity.StreamingPlanRule;

/**
 * <p>实时数据方案-规则表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface StreamingPlanRuleRepository extends BaseRepository<StreamingPlanRule, StreamingPlanRuleDTO>, ProxySelf<StreamingPlanRuleRepository> {

    /**
     * 删除
     *
     * @param planBaseId
     * @return
     */
    int deleteByParentId(Long planBaseId);
}
