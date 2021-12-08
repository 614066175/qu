package com.hand.hdsp.quality.domain.repository;

import java.util.List;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.BatchPlanDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlan;
import com.hand.hdsp.quality.domain.entity.PlanGroup;

/**
 * <p>批数据评估方案表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
public interface BatchPlanRepository extends BaseRepository<BatchPlan, BatchPlanDTO>, ProxySelf<BatchPlanRepository> {

    /**
     * 根据方案名查到所在分组
     *
     * @param batchPlanDTO
     * @return BatchPlanDTO
     */
    List<PlanGroup> listByGroup(BatchPlanDTO batchPlanDTO);

    /**
     * 批量导入评估方案
     * @param batchPlanDTOList
     */
    void batchImport(List<BatchPlanDTO> batchPlanDTOList);

    /**
     * 数据开发数据质量任务删除时，清空质量任务任务名
     * @param jobName
     * @param tenantId
     */
    void clearJobName(String jobName, Long tenantId, Long projectId);

}
