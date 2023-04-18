package com.hand.hdsp.quality.infra.mapper;

import java.util.List;

import com.hand.hdsp.quality.api.dto.ReferenceDataValueDTO;
import com.hand.hdsp.quality.api.dto.SimpleReferenceDataValueDTO;
import com.hand.hdsp.quality.domain.entity.ReferenceDataValue;
import io.choerodon.mybatis.common.BaseMapper;

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
