package com.hand.hdsp.quality.infra.mapper;

import java.util.List;

import com.hand.hdsp.quality.api.dto.NameAimExcludeDTO;
import com.hand.hdsp.quality.domain.entity.NameAimInclude;
import io.choerodon.mybatis.common.BaseMapper;

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
