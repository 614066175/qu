package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StandardStatisticsDTO;
import com.hand.hdsp.quality.domain.entity.StandardStatistics;
import com.hand.hdsp.quality.domain.repository.StandardStatisticsRepository;
import org.springframework.stereotype.Component;

/**
 * <p>标准落标表资源库实现</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-05-17 15:20:56
 */
@Component
public class StandardStatisticsRepositoryImpl extends BaseRepositoryImpl<StandardStatistics, StandardStatisticsDTO> implements StandardStatisticsRepository {

}
