package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.FieldStandardMatchsDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>字段标准匹配记录表应用服务</p>
 *
 * @author SHIJIE.GAO@HAND-CHINA.COM 2022-11-22 17:55:57
 */
public interface FieldStandardMatchsService {

    Page<FieldStandardMatchsDTO> pageAll(PageRequest pageRequest, FieldStandardMatchsDTO fieldStandardMatchsDTO);
}
