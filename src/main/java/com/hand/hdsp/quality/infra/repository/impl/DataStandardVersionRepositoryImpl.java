package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.DataStandardVersionDTO;
import com.hand.hdsp.quality.domain.entity.DataStandardVersion;
import com.hand.hdsp.quality.domain.repository.DataStandardVersionRepository;
import org.springframework.stereotype.Component;

/**
 * <p>数据标准版本表资源库实现</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
@Component
public class DataStandardVersionRepositoryImpl extends BaseRepositoryImpl<DataStandardVersion, DataStandardVersionDTO> implements DataStandardVersionRepository {

}
