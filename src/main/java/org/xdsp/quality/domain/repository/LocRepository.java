package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.LocDTO;
import org.xdsp.quality.domain.entity.Loc;

/**
 * <p>loc表资源库</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LocRepository extends BaseRepository<Loc, LocDTO>, ProxySelf<LocRepository> {

}