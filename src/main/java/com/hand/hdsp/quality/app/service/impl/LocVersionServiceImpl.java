package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.LocDTO;
import com.hand.hdsp.quality.api.dto.LocVersionDTO;
import com.hand.hdsp.quality.app.service.LocVersionService;
import com.hand.hdsp.quality.infra.mapper.LocVersionMapper;
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
    public List<LocVersionDTO> listAll(LocDTO locDTO) {
        return locVersionMapper.listAll(locDTO);

    }


}