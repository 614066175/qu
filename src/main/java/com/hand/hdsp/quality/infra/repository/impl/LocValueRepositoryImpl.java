package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.LocValueDTO;
import com.hand.hdsp.quality.domain.entity.LocValue;
import com.hand.hdsp.quality.domain.repository.LocValueRepository;
import com.hand.hdsp.quality.infra.mapper.LocValueMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>loc独立值集表资源库实现</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Component
public class LocValueRepositoryImpl extends BaseRepositoryImpl<LocValue, LocValueDTO> implements LocValueRepository {
    private LocValueMapper locValueMapper;

    public LocValueRepositoryImpl(LocValueMapper locValueMapper) {
        this.locValueMapper = locValueMapper;
    }



    @Override
    public List<LocValueDTO> queryList(LocValueDTO locValueDTO) {

        return locValueMapper.queryList(locValueDTO);
    }
}
