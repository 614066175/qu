package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.StandardGroupDTO;
import org.xdsp.quality.domain.entity.StandardGroup;

/**
 * <p>标准分组表资源库</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
public interface StandardGroupRepository extends BaseRepository<StandardGroup, StandardGroupDTO>, ProxySelf<StandardGroupRepository> {

}