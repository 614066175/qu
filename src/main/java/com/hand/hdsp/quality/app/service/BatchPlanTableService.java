package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.BatchPlanTableDTO;

/**
 * <p>批数据方案-表级规则表应用服务</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
public interface BatchPlanTableService {

    /**
     * 删除（所有相关项）
     *
     * @param batchPlanTableDTO 删除条件
     * @return 删除结果
     */
    int delete(BatchPlanTableDTO batchPlanTableDTO);
}
