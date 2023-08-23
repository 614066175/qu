package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.domain.entity.NameAimInclude;

import java.util.List;

/**
 * <p>落标包含表Mapper</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
public interface NameAimIncludeMapper extends BaseMapper<NameAimInclude> {

    /**
     * 获取落标排除项
     *
     * @param aimId 落标ID
     * @return List<NameAimExcludeDTO>
     */
    List<NameAimInclude> getListByAimId(Long aimId);
}
