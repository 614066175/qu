package com.hand.hdsp.quality.infra.mapper;

import java.util.List;

import com.hand.hdsp.quality.api.dto.NameStandardDTO;
import com.hand.hdsp.quality.domain.entity.NameStandard;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * <p>命名标准表Mapper</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
public interface NameStandardMapper extends BaseMapper<NameStandard> {

    /**
     * 获取命名标准列表
     *
     * @param nameStandardDTO 命名标准
     * @return List<NameStandardDTO>
     */
    List<NameStandardDTO> list(NameStandardDTO nameStandardDTO);

    /**
     * 获取分组ID
     *
     * @param groupCode 分组编码
     * @return Long
     */
    Long getGroupId(String groupCode);

    /**
     * 执行历史详情
     *
     * @param standardId 标准ID
     * @return NameStandardDTO
     */
    NameStandardDTO detail(Long standardId);
}
