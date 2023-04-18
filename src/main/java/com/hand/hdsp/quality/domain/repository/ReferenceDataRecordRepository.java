package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.ReferenceDataRecord;
import com.hand.hdsp.quality.api.dto.ReferenceDataRecordDTO;
import com.hand.hdsp.core.base.ProxySelf;

/**
 * <p>参考数据工作流记录表资源库</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-17 20:01:04
 */
public interface ReferenceDataRecordRepository extends BaseRepository<ReferenceDataRecord, ReferenceDataRecordDTO>, ProxySelf<ReferenceDataRecordRepository> {

}