package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.xdsp.quality.api.dto.DataStandardVersionDTO;
import org.xdsp.quality.domain.entity.DataStandardVersion;

import java.util.List;

/**
 * <p>数据标准版本表Mapper</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
public interface DataStandardVersionMapper extends BaseMapper<DataStandardVersion> {

    /**
     * @param versionId
     * @return
     */
    DataStandardVersionDTO detail(Long versionId);

    List<DataStandardVersionDTO> list(@Param("dataStandardVersionDTO") DataStandardVersionDTO dataStandardVersionDTO);
}
