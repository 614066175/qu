package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.PlanGroupDTO;

/**
 * <p>评估方案分组表应用服务</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface PlanGroupService {

    /**
     * 删除
     *
     * @param planGroupDTO 删除条件
     * @return 删除结果
     */
    int delete(PlanGroupDTO planGroupDTO);
}
