package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.ReferenceDataValueDTO;
import org.xdsp.quality.api.dto.SimpleReferenceDataValueDTO;
import org.xdsp.quality.domain.entity.ReferenceDataValue;

import java.util.List;

/**
 * <p>参考数据值Mapper</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
public interface ReferenceDataValueMapper extends BaseMapper<ReferenceDataValue> {

    /**
     * 列表查询
     * @param referenceDataValueDTO 查询参数
     * @return                      查询结果
     */
    List<ReferenceDataValueDTO> list(ReferenceDataValueDTO referenceDataValueDTO);

    /**
     *  通过参考数据ID简单查询
     * @param dataId        数据id
     * @return              简单结果
     */
    List<SimpleReferenceDataValueDTO> simpleQueryByDataId(Long dataId);
}
