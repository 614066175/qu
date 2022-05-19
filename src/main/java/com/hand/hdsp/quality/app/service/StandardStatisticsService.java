package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.StandardStatisticsDTO;

import java.util.List;

/**
 * <p>标准落标表应用服务</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-05-17 15:20:56
 */
public interface StandardStatisticsService {

    /**
     * 落标统计 模糊查询
     * @param standardStatisticsDTO
     * @return
     */
    List<StandardStatisticsDTO> listAll(StandardStatisticsDTO standardStatisticsDTO);
}
