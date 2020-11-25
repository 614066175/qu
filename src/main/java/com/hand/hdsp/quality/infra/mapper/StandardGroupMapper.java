package com.hand.hdsp.quality.infra.mapper;

import java.util.List;

import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.StandardGroup;
import com.hand.hdsp.quality.infra.vo.StandardGroupVO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

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
