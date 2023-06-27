package org.xdsp.quality.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.xdsp.quality.api.dto.DataFieldDTO;
import org.xdsp.quality.api.dto.StandardTeamDTO;

import java.util.List;

/**
 * <p>标准表应用服务</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-09 15:50:16
 */
public interface StandardTeamService {

    /**
     * 列表分页查询
     *
     * @param pageRequest
     * @param standardTeamDTO
     * @return
     */
    Page<StandardTeamDTO> list(PageRequest pageRequest, StandardTeamDTO standardTeamDTO);

    /**
     * @param standardTeamDTO
     * @return
     */
    List<StandardTeamDTO> listAll(StandardTeamDTO standardTeamDTO);

    /**
     * 删除标准组
     *
     * @param standardTeamId
     */
    void remove(Long standardTeamId);


    /**
     * 字段标准列表
     *
     * @param dataFieldDTO
     * @param pageRequest
     * @return
     */
    Page<DataFieldDTO> fieldStandardList(DataFieldDTO dataFieldDTO, PageRequest pageRequest);

    /**
     * 创建标准组
     *
     * @param standardTeamDTO
     * @return
     */
    StandardTeamDTO create(StandardTeamDTO standardTeamDTO);

    /**
     * 标准组详情
     *
     * @param standardTeamId
     * @return
     */
    StandardTeamDTO detail(Long standardTeamId);

    /**
     * 更新标准组
     *
     * @param standardTeamDTO
     * @return
     */
    StandardTeamDTO update(StandardTeamDTO standardTeamDTO);

    /**
     * 根据标准组id查询下面的字段标准
     *
     * @param standardTeamId
     * @return
     */
    List<DataFieldDTO> standardByTeamId(Long standardTeamId);

    /**
     * 查询可选父级标准组
     *
     * @param standardTeamId
     * @return
     */
    Page<StandardTeamDTO> parentTeamList(Long standardTeamId, PageRequest pageRequest);

    /**
     * @param dataFieldDTO
     * @return
     */
    Page<DataFieldDTO> standardList(DataFieldDTO dataFieldDTO, PageRequest pageRequest);
}
