package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.DataStandardVersionDTO;
import com.hand.hdsp.quality.domain.entity.DataStandardVersion;
import com.hand.hdsp.quality.domain.repository.DataStandardVersionRepository;
import org.springframework.stereotype.Component;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/25 11:32
 * @since 1.0
 */
@Component
public class DataStandardVersionRepositoryImpl extends BaseRepositoryImpl<DataStandardVersion, DataStandardVersionDTO> implements DataStandardVersionRepository {

}
