package org.xdsp.quality.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.xdsp.quality.api.dto.LocValueDTO;
import org.xdsp.quality.app.service.LocValueService;
import org.xdsp.quality.infra.mapper.LocValueMapper;

/**
 * <p>loc独立值集表应用服务默认实现</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Service
public class LocValueServiceImpl implements LocValueService {
    private LocValueMapper locValueMapper;

    public LocValueServiceImpl(LocValueMapper locValueMapper) {
        this.locValueMapper = locValueMapper;
    }

    @Override
    public Page<LocValueDTO> list(PageRequest pageRequest, LocValueDTO locValueDTO) {
         return PageHelper.doPageAndSort(pageRequest,()->locValueMapper.queryList(locValueDTO)) ;
    }
}
