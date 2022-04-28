package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.LocVersionDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * <p>loc表应用服务</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LocVersionService {


    /** 查询获取历史行表
     * @param locVersionDTO
     * @return
     */
    List<LocVersionDTO> listAll(LocVersionDTO locVersionDTO);

    /**
     * 分页查询获取历史行表
     * @param pageRequest
     * @param locVersionDTO
     * @return
     */
    Page<LocVersionDTO> list(PageRequest pageRequest, LocVersionDTO locVersionDTO);
}
