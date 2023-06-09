package org.xdsp.quality.app.service;

import org.xdsp.quality.api.dto.StreamingPlanDTO;

/**
 * <p>实时数据评估方案表应用服务</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface StreamingPlanService {

    /**
     * 删除
     *
     * @param streamingPlanDTO 删除条件
     * @return 删除结果
     */
    int delete(StreamingPlanDTO streamingPlanDTO);
}
