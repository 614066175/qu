package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.LocDTO;

import java.util.List;

/**
 * <p>loc表应用服务</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LocService {
    /**
     * 代码集发布
     *
     * @param locId
     * @return
     */

    LocDTO locRelease(Long locId);

    /**
     * 代码集详情接口
     *
     * @param locId
     * @return
     */
    LocDTO detail(Long locId);

    List<LocDTO> listAll(LocDTO locDTO);
}
