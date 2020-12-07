package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.DataFieldVersionDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>字段标准版本表应用服务</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
public interface DataFieldVersionService {

    /**
     * 字段标准详情
     *
     * @param versionId
     * @return
     */
    DataFieldVersionDTO detail(Long versionId);

    /**
     * 字段标准版本分页
     *
     * @param pageRequest
     * @param dataFieldVersionDTO
     */
    Page<DataFieldVersionDTO> list(PageRequest pageRequest, DataFieldVersionDTO dataFieldVersionDTO);
}
