package com.hand.hdsp.quality.app.service.impl;

import com.hand.hdsp.quality.api.dto.StandardStatisticsDTO;
import com.hand.hdsp.quality.app.service.StandardStatisticsService;
import com.hand.hdsp.quality.infra.mapper.StandardStatisticsMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>标准落标表应用服务默认实现</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-05-17 15:20:56
 */
@Service
public class StandardStatisticsServiceImpl implements StandardStatisticsService {

    private StandardStatisticsMapper standardStatisticsMapper;

    public StandardStatisticsServiceImpl(StandardStatisticsMapper standardStatisticsMapper) {
        this.standardStatisticsMapper = standardStatisticsMapper;
    }

    @Override
    public List<StandardStatisticsDTO> listAll(StandardStatisticsDTO standardStatisticsDTO) {
        return standardStatisticsMapper.list(standardStatisticsDTO);
    }
}
