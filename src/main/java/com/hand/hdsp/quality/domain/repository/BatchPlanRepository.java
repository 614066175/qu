package com.hand.hdsp.quality.domain.repository;

import java.util.List;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.BatchPlanDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlan;

/**
 * <p>批数据评估方案表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
public interface BatchPlanRepository extends BaseRepository<BatchPlan, BatchPlanDTO>, ProxySelf<BatchPlanRepository> {

    /**
     * 根据分组查询对应的评估方案
     *
     * @param batchPlanDTO
     * @return BatchPlanDTO
     */
    List<BatchPlanDTO> listByGroup(BatchPlanDTO batchPlanDTO);
}
