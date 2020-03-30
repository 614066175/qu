package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.BatchPlanTableDTO;
import com.hand.hdsp.quality.infra.dataobject.BatchPlanTableDO;

import java.util.List;

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

    /**
     * 创建
     *
     * @param batchPlanTableDTO
     */
    void insert(BatchPlanTableDTO batchPlanTableDTO);

    /**
     * 更新
     *
     * @param batchPlanTableDTO 更新规则信息
     */
    void update(BatchPlanTableDTO batchPlanTableDTO);

    /**
     * 表级规则列表
     *
     * @param batchPlanTableDO
     * @return
     */
    List<BatchPlanTableDO> list(BatchPlanTableDO batchPlanTableDO);

    /**
     * 表级规则明细
     *
     * @param planTableId
     * @return
     */
    BatchPlanTableDTO detail(Long planTableId);
}
