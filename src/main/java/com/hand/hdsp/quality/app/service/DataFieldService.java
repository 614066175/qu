package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>字段标准表应用服务</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
public interface DataFieldService {

    /**
     * 创建字段标准
     *
     * @param dataFieldDTO
     */
    void create(DataFieldDTO dataFieldDTO);

    /**
     * 字段标准详情
     *
     * @param tenantId
     * @param fieldId
     * @return
     */
    DataFieldDTO detail(Long tenantId, Long fieldId);

    /**
     * 删除字段标准
     *
     * @param dataFieldDTO
     */
    void delete(DataFieldDTO dataFieldDTO);

    /**
     * 字段标准分页
     *
     * @param pageRequest
     * @param dataFieldDTO
     */
    Page<DataFieldDTO> list(PageRequest pageRequest, DataFieldDTO dataFieldDTO);
}
