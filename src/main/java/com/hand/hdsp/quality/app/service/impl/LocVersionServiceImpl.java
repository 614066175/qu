package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.LocDTO;
import com.hand.hdsp.quality.api.dto.LocVersionDTO;
import com.hand.hdsp.quality.app.service.LocVersionService;
import com.hand.hdsp.quality.infra.mapper.LocVersionMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>loc表应用服务默认实现</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Service
public class LocVersionServiceImpl implements LocVersionService {


    private final LocVersionMapper locVersionMapper;

    public LocVersionServiceImpl(LocVersionMapper locVersionMapper) {
        this.locVersionMapper = locVersionMapper;
    }


    @Override
    public List<LocVersionDTO> listAll(LocVersionDTO locVersionDTO) {
        return locVersionMapper.listAll(locVersionDTO);

    }


    @Override
    public Page<LocVersionDTO> list(PageRequest pageRequest, LocVersionDTO locVersionDTO) {
        return PageHelper.doPageAndSort(pageRequest,()->locVersionMapper.listAll(locVersionDTO));
    }
}
