package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.NameStandardHistoryHead;
import com.hand.hdsp.quality.api.dto.NameStandardHistoryHeadDTO;
import com.hand.hdsp.core.base.ProxySelf;

/**
 * <p>命名标准执行历史头表资源库</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
public interface NameStandardHistoryHeadRepository extends BaseRepository<NameStandardHistoryHead, NameStandardHistoryHeadDTO>, ProxySelf<NameStandardHistoryHeadRepository> {

}