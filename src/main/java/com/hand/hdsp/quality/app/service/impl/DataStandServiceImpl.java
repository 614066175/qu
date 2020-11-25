package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.app.service.DataStandardService;
import com.hand.hdsp.quality.domain.repository.DataStandardRepository;
import org.springframework.stereotype.Service;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/25 10:40
 * @since 1.0
 */
@Service
public class DataStandServiceImpl implements DataStandardService {

    private final DataStandardRepository dataStandardRepository;

    public DataStandServiceImpl(DataStandardRepository dataStandardRepository) {
        this.dataStandardRepository = dataStandardRepository;
    }

    @Override
    public void create(DataStandardDTO dataStandardDTO) {

    }
}
