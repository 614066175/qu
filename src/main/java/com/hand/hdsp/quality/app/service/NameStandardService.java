package com.hand.hdsp.quality.app.service;

import java.util.List;

import com.hand.hdsp.quality.api.dto.NameStandardDTO;

/**
 * <p>命名标准表应用服务</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
public interface NameStandardService {

    /**
     * 删除标准
     *
     * @param nameStandardDTO 标准
     */
    void remove(NameStandardDTO nameStandardDTO);

    /**
     * 批量删除
     *
     * @param standardDtoList 标准列表
     */
    void bitchRemove(List<NameStandardDTO> standardDtoList);

    /**
     * 修改命名标准
     *
     * @param nameStandardDTO 命名标准
     * @return NameStandardDTO
     */
    NameStandardDTO update(NameStandardDTO nameStandardDTO);
}
