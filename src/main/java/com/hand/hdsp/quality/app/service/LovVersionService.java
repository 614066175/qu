package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.LovVersionDTO;

import java.util.List;

/**
 * <p>LOV表应用服务</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LovVersionService {


    /**
     * @param lovVersionDTO
     * @return
     */
    List<LovVersionDTO> listAll(LovVersionDTO lovVersionDTO);
}
