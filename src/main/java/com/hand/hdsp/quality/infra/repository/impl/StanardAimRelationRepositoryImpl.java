package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StanardAimRelationDTO;
import com.hand.hdsp.quality.domain.entity.StanardAimRelation;
import com.hand.hdsp.quality.domain.repository.StanardAimRelationRepository;
import org.springframework.stereotype.Component;

/**
 * <p>标准落标关系表资源库实现</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-30 10:23:51
 */
@Component
public class StanardAimRelationRepositoryImpl extends BaseRepositoryImpl<StanardAimRelation, StanardAimRelationDTO> implements StanardAimRelationRepository {

}
