package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.BatchPlanFieldDTO;

/**
 * <p>批数据方案-字段规则表应用服务</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface BatchPlanFieldService {

    /**
     * 删除（所有相关项）
     *
     * @param batchPlanFieldDTO 删除条件
     * @return 删除结果
     */
    int delete(BatchPlanFieldDTO batchPlanFieldDTO);
}
