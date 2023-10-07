package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.StandardRelationDTO;
import org.xdsp.quality.domain.entity.StandardRelation;
import org.xdsp.quality.domain.repository.StandardRelationRepository;

/**
 * <p>标准-标准组关系表资源库实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-09 15:50:16
 */
@Component
public class StandardRelationRepositoryImpl extends BaseRepositoryImpl<StandardRelation, StandardRelationDTO> implements StandardRelationRepository {

}
