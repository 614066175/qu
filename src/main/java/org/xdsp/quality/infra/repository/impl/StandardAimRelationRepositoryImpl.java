package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.StandardAimRelationDTO;
import org.xdsp.quality.domain.entity.StandardAimRelation;
import org.xdsp.quality.domain.repository.StandardAimRelationRepository;

/**
 * <p>标准落标关系表资源库实现</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-30 10:23:51
 */
@Component
public class StandardAimRelationRepositoryImpl extends BaseRepositoryImpl<StandardAimRelation, StandardAimRelationDTO> implements StandardAimRelationRepository {

}
