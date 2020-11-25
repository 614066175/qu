package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.DataStandardVersionDTO;
import com.hand.hdsp.quality.domain.entity.DataStandardVersion;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/25 10:45
 * @since 1.0
 */
public interface DataStandardVersionRepository extends BaseRepository<DataStandardVersion, DataStandardVersionDTO>, ProxySelf<DataStandardVersionRepository> {

}
