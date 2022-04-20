package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.LovValueDTO;
import com.hand.hdsp.quality.domain.entity.LovValue;
import com.hand.hdsp.quality.domain.repository.LovValueRepository;
import com.hand.hdsp.quality.infra.mapper.LovValueMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>LOV独立值集表资源库实现</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Component
public class LovValueRepositoryImpl extends BaseRepositoryImpl<LovValue, LovValueDTO> implements LovValueRepository {
    private LovValueMapper lovValueMapper;

    public LovValueRepositoryImpl(LovValueMapper lovValueMapper) {
        this.lovValueMapper = lovValueMapper;
    }

    @Override
    public List<LovValueDTO> query(Long lovId, String query) {

        return lovValueMapper.getLovValueDTOS(lovId,query);
    }
}
