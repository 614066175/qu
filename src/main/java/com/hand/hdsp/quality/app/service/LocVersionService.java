package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.LocVersionDTO;

import java.util.List;

/**
 * <p>loc表应用服务</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LocVersionService {


    /**
     * @param locVersionDTO
     * @return
     */
    List<LocVersionDTO> listAll(LocVersionDTO locVersionDTO);
}
