package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.BatchPlanBaseDTO;

/**
 * <p>批数据方案-基础配置表应用服务</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface BatchPlanBaseService {

    /**
     * 删除（所有相关项）
     *
     * @param batchPlanBaseDTO 删除条件
     * @return 删除结果
     */
    int delete(BatchPlanBaseDTO batchPlanBaseDTO);
}
