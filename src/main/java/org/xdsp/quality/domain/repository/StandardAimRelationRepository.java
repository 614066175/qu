package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.StandardAimRelationDTO;
import org.xdsp.quality.domain.entity.StandardAimRelation;

/**
 * <p>标准落标关系表资源库</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-30 10:23:51
 */
public interface StandardAimRelationRepository extends BaseRepository<StandardAimRelation, StandardAimRelationDTO>, ProxySelf<StandardAimRelationRepository> {

}