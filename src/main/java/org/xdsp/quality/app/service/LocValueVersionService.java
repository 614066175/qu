package org.xdsp.quality.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.xdsp.quality.api.dto.LocValueVersionDTO;

/**
 * <p>loc独立值集表应用服务</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-19 16:38:55
 */
public interface LocValueVersionService {

    Page<LocValueVersionDTO> list(PageRequest pageRequest, LocValueVersionDTO locValueVersionDTO);
}
