package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.ReferenceDataRecordDTO;
import org.xdsp.quality.domain.entity.ReferenceDataRecord;
import org.xdsp.quality.domain.repository.ReferenceDataRecordRepository;

/**
 * <p>参考数据工作流记录表资源库实现</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-17 20:01:04
 */
@Component
public class ReferenceDataRecordRepositoryImpl extends BaseRepositoryImpl<ReferenceDataRecord, ReferenceDataRecordDTO> implements ReferenceDataRecordRepository {

}
