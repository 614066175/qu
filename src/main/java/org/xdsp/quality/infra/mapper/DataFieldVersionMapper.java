package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.xdsp.quality.api.dto.DataFieldVersionDTO;
import org.xdsp.quality.domain.entity.DataFieldVersion;

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
    List<DataFieldVersionDTO> list(@Param("dataFieldVersionDTO") DataFieldVersionDTO dataFieldVersionDTO);
}
