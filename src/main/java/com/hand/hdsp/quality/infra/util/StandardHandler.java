package com.hand.hdsp.quality.infra.util;

import com.hand.hdsp.quality.api.dto.BatchPlanFieldLineDTO;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/10 14:51
 * @since 1.0
 */
public interface StandardHandler {
    /**
     * 将数据标准转换成具体的字段规则行
     *
     * @param dataStandardDTO
     * @return
     */
    BatchPlanFieldLineDTO handle(DataStandardDTO dataStandardDTO);


    /**
     * 发布时对标准进行校验
     *
     * @param dataStandardDTO
     */
    void valid(DataStandardDTO dataStandardDTO);
}
