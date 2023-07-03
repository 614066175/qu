package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.AimStatisticsDTO;
import org.xdsp.quality.domain.entity.AimStatistics;

import java.util.List;

/**
 * <p>标准落标统计表Mapper</p>
 *
 * @author guoliang.li01@hand-china.com 2022-06-16 14:38:20
 */
public interface AimStatisticsMapper extends BaseMapper<AimStatistics> {

    /**
     * 落标统计列表查询
     * @param aimStatisticsDTO
     * @return
     */
    List<AimStatisticsDTO> list(AimStatisticsDTO aimStatisticsDTO);

    /**
     * 落标统计总计
     * @param aimStatisticsDTO
     * @return
     */
    AimStatisticsDTO totalStatistic(AimStatisticsDTO aimStatisticsDTO);
}
