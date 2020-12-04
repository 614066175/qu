package com.hand.hdsp.quality.domain.repository;

import java.util.List;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.NameAim;
import com.hand.hdsp.quality.api.dto.NameAimDTO;
import com.hand.hdsp.core.base.ProxySelf;

/**
 * <p>命名落标表资源库</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
public interface NameAimRepository extends BaseRepository<NameAim, NameAimDTO>, ProxySelf<NameAimRepository> {
    /**
     * 查询落标
     *
     * @param standardId 标准ID
     * @return List<NameAimDTO>
     */
    List<NameAimDTO> list(Long standardId);
}