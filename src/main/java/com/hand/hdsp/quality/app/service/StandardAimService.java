package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.StandardAimDTO;

/**
 * <p>标准落标表应用服务</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
public interface StandardAimService {

    /**
     * 标准落标
     * @param standardAimDTO
     */
    void aim(StandardAimDTO standardAimDTO);
}
