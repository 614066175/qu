package com.hand.hdsp.quality.app.service;

import com.hand.hdsp.quality.api.dto.RootMatchHisDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>字段标准匹配记录表应用服务</p>
 *
 * @author shijie.gao@hand-china.com 2022-12-07 10:42:30
 */
public interface RootMatchHisService {

    Page<RootMatchHisDTO> pageAll(PageRequest pageRequest, RootMatchHisDTO rootMatchHisDTO);

}
