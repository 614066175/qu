package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.RootLineDTO;
import org.xdsp.quality.domain.entity.RootLine;

/**
 * 词根中文名行表资源库
 *
 * @author xin.sheng01@china-hand.com 2022-11-22 15:37:15
 */
public interface RootLineRepository extends BaseRepository<RootLine, RootLineDTO>, ProxySelf<RootLineRepository> {
    
}
