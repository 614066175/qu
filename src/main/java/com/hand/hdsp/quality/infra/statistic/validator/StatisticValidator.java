package com.hand.hdsp.quality.infra.statistic.validator;

import com.hand.hdsp.quality.api.dto.AimStatisticsDTO;
import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.api.dto.StandardAimDTO;

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
