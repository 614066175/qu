package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.DataStandardVersionDTO;
import org.xdsp.quality.domain.entity.DataStandardVersion;
import org.xdsp.quality.domain.repository.DataStandardVersionRepository;

/**
 * <p>数据标准版本表资源库实现</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
@Component
public class DataStandardVersionRepositoryImpl extends BaseRepositoryImpl<DataStandardVersion, DataStandardVersionDTO> implements DataStandardVersionRepository {

}
