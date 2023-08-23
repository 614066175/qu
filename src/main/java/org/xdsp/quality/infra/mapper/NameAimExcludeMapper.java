package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.NameAimExcludeDTO;
import org.xdsp.quality.domain.entity.NameAimExclude;

import java.util.List;

/**
 * <p>落标排除表Mapper</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
public interface NameAimExcludeMapper extends BaseMapper<NameAimExclude> {

    /**
     * 获取落标排除项
     *
     * @param aimId 落标ID
     * @return List<NameAimExcludeDTO>
     */
    List<NameAimExcludeDTO> getListByAimId(Long aimId);
}
