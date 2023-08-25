package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.LocValueVersionDTO;
import org.xdsp.quality.domain.entity.LocValueVersion;

/**
 * <p>loc独立值集表资源库</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-19 16:38:55
 */
public interface LocValueVersionRepository extends BaseRepository<LocValueVersion, LocValueVersionDTO>, ProxySelf<LocValueVersionRepository> {


}