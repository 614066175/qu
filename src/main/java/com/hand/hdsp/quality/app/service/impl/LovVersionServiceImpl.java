package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.CodeVersion;
import com.hand.hdsp.quality.app.service.LovVersionService;
import com.hand.hdsp.quality.domain.repository.LovVersionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>LOV表应用服务默认实现</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Service
public class LovVersionServiceImpl implements LovVersionService {

    private final LovVersionRepository lovVersionRepository;

    public LovVersionServiceImpl(LovVersionRepository lovVersionRepository) {
        this.lovVersionRepository = lovVersionRepository;
    }


    @Override
    public List<CodeVersion> getVersion(Long lovId) {
        return lovVersionRepository.getCodeVersion(lovId);
    }
}
