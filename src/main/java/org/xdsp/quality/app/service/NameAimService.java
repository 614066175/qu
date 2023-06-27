package org.xdsp.quality.app.service;

import org.xdsp.quality.api.dto.NameAimDTO;

import java.util.List;

/**
 * <p>命名落标表应用服务</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
public interface NameAimService {

    /**
     * 批量创建落标
     *
     * @param nameAimDtoList 落标列表
     * @return List<NameAimDTO>
     */
    List<NameAimDTO> batchCreate(List<NameAimDTO> nameAimDtoList,Long tenantId, Long projectId);

    /**
     * 删除落标
     *
     * @param primaryKey 落标主键
     */
    void remove(Long primaryKey);

    /**
     * 修改落标
     *
     * @param nameAimDTO 落标集合
     * @return NameAimDTO
     */
    NameAimDTO update(NameAimDTO nameAimDTO);

    /**
     * 批量修改落标列表
     *
     * @param nameAimDTOList 落标列表
     * @return List<NameAimDTO>
     */
    List<NameAimDTO> bitchUpdate(List<NameAimDTO> nameAimDTOList, Long tenantId, Long projectId);
}
