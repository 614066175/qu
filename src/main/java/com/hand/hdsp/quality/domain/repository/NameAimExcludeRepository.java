package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.NameAimExclude;
import com.hand.hdsp.quality.api.dto.NameAimExcludeDTO;
import com.hand.hdsp.core.base.ProxySelf;

/**
 * <p>落标排除表资源库</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
public interface NameAimExcludeRepository extends BaseRepository<NameAimExclude, NameAimExcludeDTO>, ProxySelf<NameAimExcludeRepository> {

}