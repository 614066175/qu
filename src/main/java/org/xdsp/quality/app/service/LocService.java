package org.xdsp.quality.app.service;

import org.xdsp.quality.api.dto.LocDTO;

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

    /**
     * 代码集头表模糊查询
     * @param locDTO
     * @return
     */
    List<LocDTO> listAll(LocDTO locDTO);
}
