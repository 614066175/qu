package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.DataFieldVersionDTO;
import org.xdsp.quality.domain.entity.DataFieldVersion;
import org.xdsp.quality.domain.repository.DataFieldVersionRepository;

/**
 * <p>字段标准版本表资源库实现</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
@Component
public class DataFieldVersionRepositoryImpl extends BaseRepositoryImpl<DataFieldVersion, DataFieldVersionDTO> implements DataFieldVersionRepository {

}
