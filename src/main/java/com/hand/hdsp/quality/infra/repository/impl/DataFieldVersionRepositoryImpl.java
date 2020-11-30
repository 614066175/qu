package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.DataFieldVersion;
import com.hand.hdsp.quality.api.dto.DataFieldVersionDTO;
import com.hand.hdsp.quality.domain.repository.DataFieldVersionRepository;
import org.springframework.stereotype.Component;

/**
 * <p>字段标准版本表资源库实现</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
@Component
public class DataFieldVersionRepositoryImpl extends BaseRepositoryImpl<DataFieldVersion, DataFieldVersionDTO> implements DataFieldVersionRepository {

}
