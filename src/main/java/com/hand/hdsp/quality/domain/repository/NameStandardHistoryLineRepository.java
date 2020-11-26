package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.NameStandardHistoryLine;
import com.hand.hdsp.quality.api.dto.NameStandardHistoryLineDTO;
import com.hand.hdsp.core.base.ProxySelf;

/**
 * <p>命名标准执行历史行表资源库</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
public interface NameStandardHistoryLineRepository extends BaseRepository<NameStandardHistoryLine, NameStandardHistoryLineDTO>, ProxySelf<NameStandardHistoryLineRepository> {

}