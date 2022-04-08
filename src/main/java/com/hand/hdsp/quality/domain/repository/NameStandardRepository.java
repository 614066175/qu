package com.hand.hdsp.quality.domain.repository;

import java.util.List;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.NameStandard;
import com.hand.hdsp.quality.api.dto.NameStandardDTO;
import com.hand.hdsp.core.base.ProxySelf;

/**
 * <p>命名标准表资源库</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
public interface NameStandardRepository extends BaseRepository<NameStandard, NameStandardDTO>, ProxySelf<NameStandardRepository> {

    /**
     * 获取命名标准列表
     *
     * @param nameStandardDTO 命名标准
     * @return List<NameStandardDTO>
     */
    List<NameStandardDTO> list(NameStandardDTO nameStandardDTO);

    /**
     * 批量导入
     *
     * @param nameStandardDTOList 命名标准列表
     */
    void batchImportStandard(List<NameStandardDTO> nameStandardDTOList);

    /**
     * 命名标准明细
     *
     * @param standardId 标准ID
     * @return NameStandardDTO
     */
    NameStandardDTO detail(Long standardId);
}