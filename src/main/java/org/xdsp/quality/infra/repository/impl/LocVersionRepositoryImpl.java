package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.LocVersionDTO;
import org.xdsp.quality.domain.entity.LocVersion;
import org.xdsp.quality.domain.repository.LocVersionRepository;

/**
 * <p>loc表资源库实现</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Component
public class LocVersionRepositoryImpl extends BaseRepositoryImpl<LocVersion, LocVersionDTO> implements LocVersionRepository {
    
}
