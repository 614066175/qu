package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.xdsp.quality.api.dto.StandardGroupDTO;
import org.xdsp.quality.domain.entity.StandardGroup;
import org.xdsp.quality.infra.vo.StandardGroupVO;

import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/23 21:49
 * @since 1.0
 */
public interface StandardGroupMapper  extends BaseMapper<StandardGroup> {

    /**
     * 查询分组包含标准
     * @param standardGroupVO
     * @return
     */
    List<StandardGroupDTO> listByGroup(@Param("standardGroupVO") StandardGroupVO standardGroupVO);
}
