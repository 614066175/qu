package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.DataStandardDTO;
import com.hand.hdsp.quality.domain.entity.DataStandard;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/25 10:45
 * @since 1.0
 */
public interface DataStandardRepository extends BaseRepository<DataStandard, DataStandardDTO>, ProxySelf<DataStandardRepository> {

}
