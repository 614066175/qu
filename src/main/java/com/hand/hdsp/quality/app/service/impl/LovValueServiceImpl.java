package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.LovValueDTO;
import com.hand.hdsp.quality.app.service.LovValueService;
import com.hand.hdsp.quality.domain.repository.LovValueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>LOV独立值集表应用服务默认实现</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Service
public class LovValueServiceImpl implements LovValueService {
    private LovValueRepository lovValueRepository;

    public LovValueServiceImpl(LovValueRepository lovValueRepository) {
        this.lovValueRepository = lovValueRepository;
    }

    @Override
    public List<LovValueDTO> getFuzzyQuery(Long lovId, String query) {
        return lovValueRepository.query(lovId, query);
    }
}
