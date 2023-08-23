package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.BatchPlanFieldLineDTO;
import org.xdsp.quality.domain.entity.BatchPlanFieldLine;

/**
 * <p>批数据方案-字段规则校验项表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface BatchPlanFieldLineRepository extends BaseRepository<BatchPlanFieldLine, BatchPlanFieldLineDTO>, ProxySelf<BatchPlanFieldLineRepository> {

    /**
     * 删除
     *
     * @param planRuleId
     * @return
     */
    int deleteByParentId(Long planRuleId);

    /**
     * 根据 planBaseId 删除
     *
     * @param planBaseId
     * @return
     */
    int deleteByPlanBaseId(Long planBaseId);
}
