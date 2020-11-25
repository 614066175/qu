package com.hand.hdsp.quality.infra.mapper;

import java.util.List;

import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.domain.entity.DataStandard;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/25 10:32
 * @since 1.0
 */
public interface DataStandardMapper extends BaseMapper<DataStandard> {

    /**
     * 查询数据标准列表
     * @param dataStandardDTO
     * @return
     */
    List<DataStandardDTO> list(@Param("dataStandardDTO") DataStandardDTO dataStandardDTO);
}
