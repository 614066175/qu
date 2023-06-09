package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.NameAimDTO;
import org.xdsp.quality.domain.entity.NameAim;

import java.util.List;

/**
 * <p>命名落标表Mapper</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
public interface NameAimMapper extends BaseMapper<NameAim> {

    /**
     * 查询落标
     *
     * @param standardId 标准ID
     * @return List<NameAimDTO>
     */
    List<NameAimDTO> list(Long standardId);

}
