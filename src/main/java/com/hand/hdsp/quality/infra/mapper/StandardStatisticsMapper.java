package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.StandardStatisticsDTO;
import com.hand.hdsp.quality.domain.entity.StandardStatistics;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * <p>标准落标表Mapper</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-05-17 15:20:56
 */
public interface StandardStatisticsMapper extends BaseMapper<StandardStatistics> {

    /**
     * 落标统计 模糊查询
     * @param standardStatisticsDTO
     * @return
     */
    List<StandardStatisticsDTO> list(StandardStatisticsDTO standardStatisticsDTO);
}
