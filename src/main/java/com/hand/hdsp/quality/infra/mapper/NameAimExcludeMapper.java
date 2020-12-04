package com.hand.hdsp.quality.infra.mapper;

import java.util.List;

import com.hand.hdsp.quality.api.dto.NameAimExcludeDTO;
import com.hand.hdsp.quality.domain.entity.NameAimExclude;
import io.choerodon.mybatis.common.BaseMapper;

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
