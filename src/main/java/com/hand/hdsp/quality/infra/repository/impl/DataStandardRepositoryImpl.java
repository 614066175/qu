package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import com.hand.hdsp.quality.domain.repository.DataStandardRepository;
import org.springframework.stereotype.Component;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/25 11:31
 * @since 1.0
 */
@Component
public class DataStandardRepositoryImpl extends BaseRepositoryImpl<DataStandard, DataStandardDTO> implements DataStandardRepository {

}
