package com.hand.hdsp.quality.infra.mapper;

import java.util.List;

import com.hand.hdsp.quality.api.dto.NameStandardDTO;
import com.hand.hdsp.quality.domain.entity.NameStandard;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * <p>命名标准表Mapper</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
public interface NameStandardMapper extends BaseMapper<NameStandard> {

    /**
     * 获取命名标准列表
     *
     * @param nameStandardDTO 命名标准
     * @return List<NameStandardDTO>
     */
    List<NameStandardDTO> list(NameStandardDTO nameStandardDTO);
}
