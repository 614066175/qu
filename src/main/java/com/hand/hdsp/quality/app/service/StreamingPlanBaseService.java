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
}
