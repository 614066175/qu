package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.StreamingPlanBaseDTO;

/**
 * <p>实时数据方案-基础配置表应用服务</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface StreamingPlanBaseService {

    /**
     * 删除（所有相关项）
     *
     * @param streamingPlanBaseDTO 删除条件
     * @return 删除结果
     */
    int delete(StreamingPlanBaseDTO streamingPlanBaseDTO);

    /**
     * 更新
     *
     * @param streamingPlanBaseDTO 更新信息
     */
    void update(StreamingPlanBaseDTO streamingPlanBaseDTO);

    /**
     * 新建
     *
     * @param streamingPlanBaseDTO
     */
    void insert(StreamingPlanBaseDTO streamingPlanBaseDTO);

    /**
     * 关联查询规则校验项、告警等级
     *
     * @param planBaseId
     * @return
     */
    StreamingPlanBaseDTO detail(Long planBaseId);
}
