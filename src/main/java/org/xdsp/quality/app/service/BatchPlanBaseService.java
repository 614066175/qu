package org.xdsp.quality.app.service;

import org.xdsp.quality.api.dto.BatchPlanBaseDTO;
import org.xdsp.quality.api.dto.ColumnDTO;

import java.util.List;

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

    /**
     * 基础配置表明细
     *
     * @param planBaseId
     * @param currentPlanId
     * @param tenantId
     * @return
     */
    BatchPlanBaseDTO detail(Long planBaseId, Long currentPlanId, Long tenantId);

    /**
     * 解析自定义SQL
     *
     * @param sql
     * @return
     */
    List<ColumnDTO> columns(String sql);

    /**
     * 取消分配
     *
     * @param batchPlanBaseDTO
     */
    void cancelAssign(BatchPlanBaseDTO batchPlanBaseDTO);

    /**
     * 创建质检项
     *
     * @param batchPlanBaseDTO
     * @return
     */
    BatchPlanBaseDTO create(BatchPlanBaseDTO batchPlanBaseDTO);

    /**
     * 更新质检项
     *
     * @param batchPlanBaseDTO
     * @return
     */
    BatchPlanBaseDTO update(BatchPlanBaseDTO batchPlanBaseDTO);
}
