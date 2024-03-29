package org.xdsp.quality.app.service;

import org.xdsp.quality.api.dto.BatchPlanDTO;

import java.util.List;

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

    /**
     * 执行批数据评估方案
     *
     * @param tenantId tenantId
     * @param planId   planId
     * @return Long
     */
    Long exec(Long tenantId, Long planId, Long projectId);

    /**
     * 生成数据质量任务
     *
     * @param tenantId
     * @param planId
     */
    void generate(Long tenantId, Long projectId, Long planId);

    /**
     * 根据方案发送告警消息
     *
     * @param planId
     * @param resultId
     */
    void sendMessage(Long planId, Long resultId);

    /**
     * @param planCode
     * @param tenantId
     * @param projectId
     */
    void clearExceptionData(String planCode, Long tenantId, Long projectId);

    /**
     * 修复项目共享接口
     *
     */
    void fixProjectShare();

    /**
     * 批量生成
     * @param tenantId
     * @param projectId
     * @param planIds
     */
    void batchGenerate(Long tenantId, Long projectId, List<Long> planIds);
}
