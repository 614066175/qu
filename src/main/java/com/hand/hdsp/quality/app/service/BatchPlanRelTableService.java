package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.BatchPlanRelTableDTO;

/**
 * <p>批数据方案-表间规则表应用服务</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface BatchPlanRelTableService {

    /**
     * 删除（所有相关项）
     *
     * @param batchPlanRelTableDTO 删除条件
     * @return 删除结果
     */
    int delete(BatchPlanRelTableDTO batchPlanRelTableDTO);
}
