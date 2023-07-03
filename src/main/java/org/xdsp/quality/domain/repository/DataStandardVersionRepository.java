package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.DataStandardVersionDTO;
import org.xdsp.quality.domain.entity.DataStandardVersion;

/**
 * <p>数据标准版本表资源库</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
public interface DataStandardVersionRepository extends BaseRepository<DataStandardVersion, DataStandardVersionDTO>, ProxySelf<DataStandardVersionRepository> {

}