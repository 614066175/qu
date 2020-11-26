package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.NameStandard;
import com.hand.hdsp.quality.api.dto.NameStandardDTO;
import com.hand.hdsp.core.base.ProxySelf;

/**
 * <p>命名标准表资源库</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
public interface NameStandardRepository extends BaseRepository<NameStandard, NameStandardDTO>, ProxySelf<NameStandardRepository> {

}