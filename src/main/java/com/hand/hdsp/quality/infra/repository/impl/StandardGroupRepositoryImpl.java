package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.domain.repository.StandardGroupRepository;
import org.springframework.stereotype.Component;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/25 11:29
 * @since 1.0
 */
@Component
public class StandardGroupRepositoryImpl extends BaseRepositoryImpl<StandardGroup, StandardGroupDTO> implements StandardGroupRepository {

}
