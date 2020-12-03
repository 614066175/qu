package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.DataStandardVersionDTO;

/**
 * <p>数据标准版本表应用服务</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
public interface DataStandardVersionService {

    /**
     * 数据标准历史表数据
     * @param versionId
     * @return
     */
    DataStandardVersionDTO detail(Long versionId);
}
