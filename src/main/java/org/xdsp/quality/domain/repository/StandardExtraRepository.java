package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.StandardExtraDTO;
import org.xdsp.quality.domain.entity.StandardExtra;

/**
 * <p>标准附加信息表资源库</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:45
 */
public interface StandardExtraRepository extends BaseRepository<StandardExtra, StandardExtraDTO>, ProxySelf<StandardExtraRepository> {

}