package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.LovValueDTO;

import java.util.List;

/**
 * <p>LOV独立值集表应用服务</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LovValueService {

    List<LovValueDTO> getFuzzyQuery(Long lovId, String query);
}
