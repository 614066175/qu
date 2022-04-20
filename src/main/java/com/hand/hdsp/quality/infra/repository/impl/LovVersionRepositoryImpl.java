package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.CodeVersion;
import com.hand.hdsp.quality.api.dto.LovVersionDTO;
import com.hand.hdsp.quality.domain.entity.LovVersion;
import com.hand.hdsp.quality.domain.repository.LovVersionRepository;
import com.hand.hdsp.quality.infra.mapper.LovVersionMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>LOV表资源库实现</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Component
public class LovVersionRepositoryImpl extends BaseRepositoryImpl<LovVersion, LovVersionDTO> implements LovVersionRepository {


    private LovVersionMapper lovVersionMapper;

    public LovVersionRepositoryImpl(LovVersionMapper lovVersionMapper) {
        this.lovVersionMapper = lovVersionMapper;
    }

    @Override
    public List<CodeVersion> getCodeVersion(Long lovId) {
        return lovVersionMapper.getCodeVersin(lovId);
    }
}
