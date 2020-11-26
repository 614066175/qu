package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.NameStandardContentLine;
import com.hand.hdsp.quality.api.dto.NameStandardContentLineDTO;
import com.hand.hdsp.core.base.ProxySelf;

/**
 * <p>命名标准落标行表资源库</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
public interface NameStandardContentLineRepository extends BaseRepository<NameStandardContentLine, NameStandardContentLineDTO>, ProxySelf<NameStandardContentLineRepository> {

}