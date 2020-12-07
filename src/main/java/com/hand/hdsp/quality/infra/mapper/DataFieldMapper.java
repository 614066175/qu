package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.domain.entity.DataField;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>字段标准表Mapper</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
public interface DataFieldMapper extends BaseMapper<DataField> {

    /**
     * 查询数据标准列表
     * @param dataFieldDTO
     * @return
     */
    List<DataFieldDTO> list(@Param("dataFieldDTO") DataFieldDTO dataFieldDTO);

}
