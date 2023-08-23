package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.BatchPlanTableLineDTO;
import org.xdsp.quality.domain.entity.BatchPlanTableLine;

/**
 * <p>批数据方案-表级规则校验项表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
public interface BatchPlanTableLineRepository extends BaseRepository<BatchPlanTableLine, BatchPlanTableLineDTO>, ProxySelf<BatchPlanTableLineRepository> {

    /**
     * 删除
     *
     * @param planRuleId
     * @return
     */
    int deleteByPlanRuleId(Long planRuleId);

    /**
     * 根据 planBaseId 删除
     *
     * @param planBaseId
     * @return
     */
    int deleteByPlanBaseId(Long planBaseId);
}
