package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.StandardRelationDTO;
import org.xdsp.quality.domain.entity.StandardRelation;

/**
 * <p>标准-标准组关系表资源库</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-09 15:50:16
 */
public interface StandardRelationRepository extends BaseRepository<StandardRelation, StandardRelationDTO>, ProxySelf<StandardRelationRepository> {

}