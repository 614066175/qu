package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.BatchPlanRelTableDTO;
import com.hand.hdsp.quality.api.dto.BatchPlanTableDTO;

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

    /**
     * 表间规则明细
     *
     * @param planRelTableId
     * @return
     */
    BatchPlanRelTableDTO detail(Long planRelTableId);

    /**
     * 创建
     *
     * @param batchPlanRelTableDTO
     */
    void insert(BatchPlanRelTableDTO batchPlanRelTableDTO);

    /**
     * 更新
     *
     * @param batchPlanRelTableDTO 更新规则信息
     */
    void update(BatchPlanRelTableDTO batchPlanRelTableDTO);
}
