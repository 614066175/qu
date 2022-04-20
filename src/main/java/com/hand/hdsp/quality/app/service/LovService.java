package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.LovDTO;
import com.hand.hdsp.quality.api.dto.LovValueDTO;

import java.util.List;

/**
 * <p>LOV表应用服务</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LovService {

    LovDTO lovRelease(Long lovId);

    void AssertOpen(Long lovId);


}
