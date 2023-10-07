package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.LocValueVersionDTO;
import org.xdsp.quality.domain.entity.LocValueVersion;
import org.xdsp.quality.domain.repository.LocValueVersionRepository;

/**
 * <p>loc独立值集表资源库实现</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-19 16:38:55
 */
@Component
public class LocValueVersionRepositoryImpl extends BaseRepositoryImpl<LocValueVersion, LocValueVersionDTO> implements LocValueVersionRepository {

}
