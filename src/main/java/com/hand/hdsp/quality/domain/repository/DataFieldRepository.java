package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.DataField;
import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.core.base.ProxySelf;

/**
 * <p>字段标准表资源库</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
public interface DataFieldRepository extends BaseRepository<DataField, DataFieldDTO>, ProxySelf<DataFieldRepository> {

}