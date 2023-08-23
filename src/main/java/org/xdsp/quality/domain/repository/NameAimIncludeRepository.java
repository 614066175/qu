package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.NameAimIncludeDTO;
import org.xdsp.quality.domain.entity.NameAimInclude;

/**
 * <p>落标包含表资源库</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
public interface NameAimIncludeRepository extends BaseRepository<NameAimInclude, NameAimIncludeDTO>, ProxySelf<NameAimIncludeRepository> {

}