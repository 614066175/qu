package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.LocValueDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * <p>loc独立值集表应用服务</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LocValueService {


    /**
     * 代码集值列表查询
     * @param pageRequest
     * @param locValueDTO
     * @return
     */
    Page<LocValueDTO> list(PageRequest pageRequest, LocValueDTO locValueDTO);

}
