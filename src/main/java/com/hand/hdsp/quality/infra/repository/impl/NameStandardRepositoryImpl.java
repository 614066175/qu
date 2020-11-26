package com.hand.hdsp.quality.infra.repository.impl;

import java.util.List;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.NameStandard;
import com.hand.hdsp.quality.api.dto.NameStandardDTO;
import com.hand.hdsp.quality.domain.repository.NameStandardRepository;
import com.hand.hdsp.quality.infra.mapper.NameStandardMapper;
import org.springframework.stereotype.Component;

/**
 * <p>命名标准表资源库实现</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@Component
public class NameStandardRepositoryImpl extends BaseRepositoryImpl<NameStandard, NameStandardDTO> implements NameStandardRepository {

    private final NameStandardMapper nameStandardMapper;

    public NameStandardRepositoryImpl(NameStandardMapper nameStandardMapper) {
        this.nameStandardMapper = nameStandardMapper;
    }

    @Override
    public List<NameStandardDTO> list(NameStandardDTO nameStandardDTO) {
        return nameStandardMapper.list(nameStandardDTO);
    }
}
