package org.xdsp.quality.domain.repository;


import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.RootVersionDTO;
import org.xdsp.quality.domain.entity.RootVersion;

import java.util.List;

/**
 * 词根版本资源库
 *
 * @author xin.sheng01@china-hand.com 2022-11-21 14:28:19
 */
public interface RootVersionRepository extends BaseRepository<RootVersion, RootVersionDTO>, ProxySelf<RootVersionRepository> {
    List<RootVersion> list(RootVersion rootVersion);

    RootVersion detail(Long id);
}
