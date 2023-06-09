package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.NameAimDTO;
import org.xdsp.quality.domain.entity.NameAim;

import java.util.List;

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