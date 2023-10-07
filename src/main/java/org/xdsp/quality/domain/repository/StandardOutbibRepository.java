package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.StandardOutbibDTO;
import org.xdsp.quality.domain.entity.StandardOutbib;

/**
 * <p>标准落标表资源库</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-25 17:44:46
 */
public interface StandardOutbibRepository extends BaseRepository<StandardOutbib, StandardOutbibDTO>, ProxySelf<StandardOutbibRepository> {

}