package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.NameAimDTO;
import org.xdsp.quality.domain.entity.NameAim;
import org.xdsp.quality.domain.repository.NameAimRepository;
import org.xdsp.quality.infra.mapper.NameAimMapper;

import java.util.List;

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
