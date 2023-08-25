package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.StandardAimDTO;
import org.xdsp.quality.domain.entity.StandardAim;

/**
 * <p>标准落标表资源库</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-30 10:23:51
 */
public interface StandardAimRepository extends BaseRepository<StandardAim, StandardAimDTO>, ProxySelf<StandardAimRepository> {

}