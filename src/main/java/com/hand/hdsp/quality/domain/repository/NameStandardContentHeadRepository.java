package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.NameStandardContentHead;
import com.hand.hdsp.quality.api.dto.NameStandardContentHeadDTO;
import com.hand.hdsp.core.base.ProxySelf;

/**
 * <p>命名标准落标头表资源库</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
public interface NameStandardContentHeadRepository extends BaseRepository<NameStandardContentHead, NameStandardContentHeadDTO>, ProxySelf<NameStandardContentHeadRepository> {

}