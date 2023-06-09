package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.ReferenceDataRecordDTO;
import org.xdsp.quality.domain.entity.ReferenceDataRecord;

/**
 * <p>参考数据工作流记录表资源库</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-17 20:01:04
 */
public interface ReferenceDataRecordRepository extends BaseRepository<ReferenceDataRecord, ReferenceDataRecordDTO>, ProxySelf<ReferenceDataRecordRepository> {

}