package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.DataFieldVersionDTO;
import org.xdsp.quality.domain.entity.DataFieldVersion;

/**
 * <p>字段标准版本表资源库</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
public interface DataFieldVersionRepository extends BaseRepository<DataFieldVersion, DataFieldVersionDTO>, ProxySelf<DataFieldVersionRepository> {

}