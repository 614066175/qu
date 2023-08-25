package org.xdsp.quality.app.service;

import org.xdsp.quality.api.dto.StandardExtraDTO;

import java.util.List;

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
