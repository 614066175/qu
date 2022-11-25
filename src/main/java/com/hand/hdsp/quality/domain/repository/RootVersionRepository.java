package com.hand.hdsp.quality.domain.repository;


import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.RootVersionDTO;
import com.hand.hdsp.quality.domain.entity.RootVersion;

/**
 * 词根版本资源库
 *
 * @author xin.sheng01@china-hand.com 2022-11-21 14:28:19
 */
public interface RootVersionRepository extends BaseRepository<RootVersion, RootVersionDTO>, ProxySelf<RootVersionRepository> {
    
}
