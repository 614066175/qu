package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.ExtraVersionDTO;
import com.hand.hdsp.quality.domain.entity.ExtraVersion;

/**
 * <p>附加信息版本表资源库</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:45
 */
public interface ExtraVersionRepository extends BaseRepository<ExtraVersion, ExtraVersionDTO>, ProxySelf<ExtraVersionRepository> {

}