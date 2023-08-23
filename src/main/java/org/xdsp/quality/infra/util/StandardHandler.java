package org.xdsp.quality.infra.util;

import org.xdsp.quality.api.dto.BatchPlanFieldLineDTO;
import org.xdsp.quality.api.dto.DataFieldDTO;
import org.xdsp.quality.api.dto.DataStandardDTO;

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

    /**
     * 字段标准转换为具体的字段规则行
     * @param dataFieldDTO
     * @param fieldType
     * @return
     */
    BatchPlanFieldLineDTO handle(DataFieldDTO dataFieldDTO,String fieldType);
}
