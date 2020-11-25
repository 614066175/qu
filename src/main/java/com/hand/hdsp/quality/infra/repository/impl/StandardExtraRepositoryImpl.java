package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StandardExtraDTO;
import com.hand.hdsp.quality.domain.entity.StandardExtra;
import com.hand.hdsp.quality.domain.repository.StandardExtraRepository;
import org.springframework.stereotype.Component;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/25 11:33
 * @since 1.0
 */
@Component
public class StandardExtraRepositoryImpl extends BaseRepositoryImpl<StandardExtra, StandardExtraDTO> implements StandardExtraRepository {

}
