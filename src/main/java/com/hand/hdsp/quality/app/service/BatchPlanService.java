package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.BatchPlanDTO;

/**
 * <p>批数据评估方案表应用服务</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
public interface BatchPlanService {

    /**
     * 删除
     *
     * @param batchPlanDTO 删除条件
     * @return 删除结果
     */
    int delete(BatchPlanDTO batchPlanDTO);
}
