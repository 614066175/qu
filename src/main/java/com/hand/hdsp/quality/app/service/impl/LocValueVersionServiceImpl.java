package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.LocValueVersionDTO;
import com.hand.hdsp.quality.app.service.LocValueVersionService;
import com.hand.hdsp.quality.infra.mapper.LocValueVersionMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * <p>loc独立值集表应用服务默认实现</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-19 16:38:55
 */
@Service
public class LocValueVersionServiceImpl implements LocValueVersionService {

    private LocValueVersionMapper locValueVersionMapper;

    public LocValueVersionServiceImpl(LocValueVersionMapper locValueVersionMapper) {
        this.locValueVersionMapper = locValueVersionMapper;
    }

    @Override
    public Page<LocValueVersionDTO> list(PageRequest pageRequest, LocValueVersionDTO locValueVersionDTO) {
        return PageHelper.doPageAndSort(pageRequest,()->locValueVersionMapper.getList(locValueVersionDTO)) ;
    }
}
