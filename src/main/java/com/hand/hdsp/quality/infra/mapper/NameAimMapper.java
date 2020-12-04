package com.hand.hdsp.quality.infra.mapper;

import java.util.List;

import com.hand.hdsp.quality.api.dto.NameAimDTO;
import com.hand.hdsp.quality.domain.entity.NameAim;
import io.choerodon.mybatis.common.BaseMapper;

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
