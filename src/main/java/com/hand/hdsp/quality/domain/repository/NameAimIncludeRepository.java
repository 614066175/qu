package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.NameAimInclude;
import com.hand.hdsp.quality.api.dto.NameAimIncludeDTO;
import com.hand.hdsp.core.base.ProxySelf;

/**
 * <p>落标包含表资源库</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
public interface NameAimIncludeRepository extends BaseRepository<NameAimInclude, NameAimIncludeDTO>, ProxySelf<NameAimIncludeRepository> {

}