package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.LocVersionDTO;

import java.util.List;

/**
 * <p>loc表应用服务</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LocVersionService {


    /** 获取历史版本表
     * @param locVersionDTO
     * @return
     */
    List<LocVersionDTO> listAll(LocVersionDTO locVersionDTO);

    /**
     * 获取更新人账户名
     * @param lastUpdatedBy
     * @return
     */
    String getUserName(Long lastUpdatedBy);
}
