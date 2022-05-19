package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.StandardStatisticsDTO;
import com.hand.hdsp.quality.domain.entity.StandardStatistics;

/**
 * <p>标准落标表资源库</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-05-17 15:20:56
 */
public interface StandardStatisticsRepository extends BaseRepository<StandardStatistics, StandardStatisticsDTO>, ProxySelf<StandardStatisticsRepository> {

}