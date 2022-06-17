package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.AimStatisticsDTO;
import com.hand.hdsp.quality.domain.entity.AimStatistics;
import com.hand.hdsp.quality.domain.repository.AimStatisticsRepository;
import org.springframework.stereotype.Component;

/**
 * <p>标准落标统计表资源库实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-06-16 14:38:20
 */
@Component
public class AimStatisticsRepositoryImpl extends BaseRepositoryImpl<AimStatistics, AimStatisticsDTO> implements AimStatisticsRepository {

}
