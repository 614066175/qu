package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.RootLineDTO;
import com.hand.hdsp.quality.domain.entity.RootLine;

/**
 * 词根中文名行表资源库
 *
 * @author xin.sheng01@china-hand.com 2022-11-22 15:37:15
 */
public interface RootLineRepository extends BaseRepository<RootLine, RootLineDTO>, ProxySelf<RootLineRepository> {
    
}
