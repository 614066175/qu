package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.DataFieldVersionDTO;
import com.hand.hdsp.quality.domain.entity.DataFieldVersion;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>字段标准版本表Mapper</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
public interface DataFieldVersionMapper extends BaseMapper<DataFieldVersion> {

    /**
     * @param versionId int
     * @return DataFieldVersionDTO
     */
    DataFieldVersionDTO detail(Long versionId);

    /**
     * @param dataFieldVersionDTO DataFieldVersionDTO
     * @return List<DataFieldVersionDTO>
     */
    List<DataFieldVersionDTO> list(@Param("dataStandardVersionDTO") DataFieldVersionDTO dataFieldVersionDTO);
}
