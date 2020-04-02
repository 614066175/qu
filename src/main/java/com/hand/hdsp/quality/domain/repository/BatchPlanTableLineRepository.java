package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.BatchPlanTableLineDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanTableLine;

/**
 * <p>批数据方案-表级规则校验项表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
public interface BatchPlanTableLineRepository extends BaseRepository<BatchPlanTableLine, BatchPlanTableLineDTO>, ProxySelf<BatchPlanTableLineRepository> {

    /**
     * 删除
     *
     * @param planTableId
     * @return
     */
    int deleteByParentId(Long planTableId);
}
