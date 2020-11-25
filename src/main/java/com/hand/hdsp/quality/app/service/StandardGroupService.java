package com.hand.hdsp.quality.app.service;

import java.util.List;

import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.infra.vo.StandardGroupVO;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/23 20:58
 * @since 1.0
 */
public interface StandardGroupService {

    /**
     * 根据分组名或标准名查询
     * @param standardGroupVO
     * @return
     */
    List<StandardGroupDTO> listByGroup(StandardGroupVO standardGroupVO);

    /**
     * 删除分组
     * @param standardGroupDTO
     */
    void delete(StandardGroupDTO standardGroupDTO);
}
