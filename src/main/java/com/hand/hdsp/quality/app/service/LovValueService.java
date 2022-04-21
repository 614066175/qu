package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.LovValueDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * <p>LOV独立值集表应用服务</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LovValueService {

    /**
     * 代码集明细模糊查询
     * @param lovId
     * @param query
     * @return
     */
    List<LovValueDTO> getFuzzyQuery(Long lovId, String query);

    /**
     * 代码集值列表查询
     * @param pageRequest
     * @param lovValueDTO
     * @return
     */
    Page<LovValueDTO> list(PageRequest pageRequest, LovValueDTO lovValueDTO);

}
