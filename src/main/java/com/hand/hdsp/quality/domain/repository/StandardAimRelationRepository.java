package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.StandardAimRelationDTO;
import com.hand.hdsp.quality.domain.entity.StandardAimRelation;

/**
 * <p>标准落标关系表资源库</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-30 10:23:51
 */
public interface StandardAimRelationRepository extends BaseRepository<StandardAimRelation, StandardAimRelationDTO>, ProxySelf<StandardAimRelationRepository> {

}