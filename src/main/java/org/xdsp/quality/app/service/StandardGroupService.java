package org.xdsp.quality.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.poi.ss.formula.functions.T;
import org.hzero.export.vo.ExportParam;
import org.xdsp.quality.api.dto.StandardGroupDTO;
import org.xdsp.quality.infra.vo.StandardGroupVO;

import java.util.List;

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

    /**
     * 查询列表
     * @param pageRequest
     * @param standardGroupVO
     * @return
     */
    Page<T> list(PageRequest pageRequest, StandardGroupVO standardGroupVO);

    /**
     *
     * @param dto
     * @param exportParam
     * @return
     */
    List<StandardGroupDTO> export(StandardGroupDTO dto, ExportParam exportParam);

    /**
     * 新建分组
     * @param standardGroupDTO
     * @return
     */
    int create(StandardGroupDTO standardGroupDTO);

    /**
     * 修改分组
     * @param standardGroupDTO
     * @return
     */
    StandardGroupDTO update(StandardGroupDTO standardGroupDTO);
}
