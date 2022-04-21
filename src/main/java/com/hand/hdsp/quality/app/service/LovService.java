package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.LovDTO;

/**
 * <p>LOV表应用服务</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LovService {
    /**
     * 代码集发布
     *
     * @param lovId
     * @return
     */

    LovDTO lovRelease(Long lovId);

    /**
     * 是否发布代码集
     *
     * @param lovId
     */
    int AssertOpen(Long lovId);


}
