package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.xdsp.quality.api.dto.NameStandardDTO;
import org.xdsp.quality.domain.entity.NameStandard;

import java.util.List;

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

    /**
     * 根据责任人名称去查找员工id
     * @return 员工id
     */
    List<Long> checkCharger(String chargeName, Long tenantId);
}
