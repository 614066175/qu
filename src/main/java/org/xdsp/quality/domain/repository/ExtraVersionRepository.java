package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.ExtraVersionDTO;
import org.xdsp.quality.domain.entity.ExtraVersion;

/**
 * <p>附加信息版本表资源库</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:45
 */
public interface ExtraVersionRepository extends BaseRepository<ExtraVersion, ExtraVersionDTO>, ProxySelf<ExtraVersionRepository> {

}