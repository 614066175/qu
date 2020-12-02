package com.hand.hdsp.quality.app.service;

import java.util.List;

import com.hand.hdsp.quality.api.dto.StandardExtraDTO;

/**
 * <p>标准附加信息表应用服务</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:45
 */
public interface StandardExtraService {



    /**
     * @param standardExtraDTOList
     */
    void batchUpdate(List<StandardExtraDTO> standardExtraDTOList);

}
