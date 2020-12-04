package com.hand.hdsp.quality.infra.repository.impl;

import java.util.List;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.NameAim;
import com.hand.hdsp.quality.api.dto.NameAimDTO;
import com.hand.hdsp.quality.domain.repository.NameAimRepository;
import com.hand.hdsp.quality.infra.mapper.NameAimMapper;
import org.springframework.stereotype.Component;

/**
 * <p>命名落标表资源库实现</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Component
public class NameAimRepositoryImpl extends BaseRepositoryImpl<NameAim, NameAimDTO> implements NameAimRepository {

    private final NameAimMapper nameAimMapper;

    public NameAimRepositoryImpl(NameAimMapper nameAimMapper) {
        this.nameAimMapper = nameAimMapper;
    }

    @Override
    public List<NameAimDTO> list(Long standardId) {
        return nameAimMapper.list(standardId);
    }
}
