package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.NameAimExcludeDTO;
import org.xdsp.quality.domain.entity.NameAimExclude;

/**
 * <p>落标排除表资源库</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
public interface NameAimExcludeRepository extends BaseRepository<NameAimExclude, NameAimExcludeDTO>, ProxySelf<NameAimExcludeRepository> {

}