package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.DataStandardDTO;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/25 10:38
 * @since 1.0
 */
public interface DataStandardService {

    /**
     * 创建数据标准
     * @param dataStandardDTO
     */
    void create(DataStandardDTO dataStandardDTO);
}
