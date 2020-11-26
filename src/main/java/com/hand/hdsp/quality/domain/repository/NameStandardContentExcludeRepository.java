package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.NameStandardContentExclude;
import com.hand.hdsp.quality.api.dto.NameStandardContentExcludeDTO;
import com.hand.hdsp.core.base.ProxySelf;

/**
 * <p>命名标准落标排除表资源库</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
public interface NameStandardContentExcludeRepository extends BaseRepository<NameStandardContentExclude, NameStandardContentExcludeDTO>, ProxySelf<NameStandardContentExcludeRepository> {

}