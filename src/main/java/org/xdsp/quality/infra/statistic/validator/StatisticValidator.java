package org.xdsp.quality.infra.statistic.validator;

import org.xdsp.quality.api.dto.AimStatisticsDTO;
import org.xdsp.quality.api.dto.DataFieldDTO;
import org.xdsp.quality.api.dto.StandardAimDTO;

/**
 * <p>
 * 统计验证器
 * </p>
 *
 * @author lgl 2022/6/16 17:01
 * @since 1.0
 */
public interface StatisticValidator {

    /**
     * 根据落标记录对字段标准进行验证
     *
     * @param dataFieldDTO
     * @param standardAimDTO
     * @param aimStatisticsDTO
     * @return
     */
    boolean valid(DataFieldDTO dataFieldDTO, StandardAimDTO standardAimDTO, AimStatisticsDTO aimStatisticsDTO);
}
