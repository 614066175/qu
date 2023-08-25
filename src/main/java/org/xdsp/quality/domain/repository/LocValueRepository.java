package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.LocValueDTO;
import org.xdsp.quality.domain.entity.LocValue;

/**
 * <p>loc独立值集表资源库</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LocValueRepository extends BaseRepository<LocValue, LocValueDTO>, ProxySelf<LocValueRepository> {



}