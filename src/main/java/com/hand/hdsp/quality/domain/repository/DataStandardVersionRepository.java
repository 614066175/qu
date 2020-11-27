package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.DataStandardVersionDTO;
import com.hand.hdsp.quality.domain.entity.DataStandardVersion;

/**
 * <p>数据标准版本表资源库</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
public interface DataStandardVersionRepository extends BaseRepository<DataStandardVersion, DataStandardVersionDTO>, ProxySelf<DataStandardVersionRepository> {

}