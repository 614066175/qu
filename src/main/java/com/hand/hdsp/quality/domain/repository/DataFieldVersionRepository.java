package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.DataFieldVersion;
import com.hand.hdsp.quality.api.dto.DataFieldVersionDTO;
import com.hand.hdsp.core.base.ProxySelf;

/**
 * <p>字段标准版本表资源库</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
public interface DataFieldVersionRepository extends BaseRepository<DataFieldVersion, DataFieldVersionDTO>, ProxySelf<DataFieldVersionRepository> {

}