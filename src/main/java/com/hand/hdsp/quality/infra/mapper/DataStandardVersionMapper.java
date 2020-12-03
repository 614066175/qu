package com.hand.hdsp.quality.infra.mapper;

import java.util.List;

import com.hand.hdsp.quality.api.dto.DataStandardVersionDTO;
import com.hand.hdsp.quality.domain.entity.DataStandardVersion;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

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
