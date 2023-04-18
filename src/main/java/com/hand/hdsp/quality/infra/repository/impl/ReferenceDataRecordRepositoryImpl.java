package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.ReferenceDataRecord;
import com.hand.hdsp.quality.api.dto.ReferenceDataRecordDTO;
import com.hand.hdsp.quality.domain.repository.ReferenceDataRecordRepository;
import org.springframework.stereotype.Component;

/**
 * <p>参考数据工作流记录表资源库实现</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-17 20:01:04
 */
@Component
public class ReferenceDataRecordRepositoryImpl extends BaseRepositoryImpl<ReferenceDataRecord, ReferenceDataRecordDTO> implements ReferenceDataRecordRepository {

}
