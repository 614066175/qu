package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.StandardRelation;
import com.hand.hdsp.quality.api.dto.StandardRelationDTO;
import com.hand.hdsp.quality.domain.repository.StandardRelationRepository;
import org.springframework.stereotype.Component;

/**
 * <p>标准-标准组关系表资源库实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-09 15:50:16
 */
@Component
public class StandardRelationRepositoryImpl extends BaseRepositoryImpl<StandardRelation, StandardRelationDTO> implements StandardRelationRepository {

}
