package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StandardAimRelationDTO;
import com.hand.hdsp.quality.domain.entity.StandardAimRelation;
import com.hand.hdsp.quality.domain.repository.StandardAimRelationRepository;
import org.springframework.stereotype.Component;

/**
 * <p>标准落标关系表资源库实现</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-30 10:23:51
 */
@Component
public class StandardAimRelationRepositoryImpl extends BaseRepositoryImpl<StandardAimRelation, StandardAimRelationDTO> implements StandardAimRelationRepository {

}
