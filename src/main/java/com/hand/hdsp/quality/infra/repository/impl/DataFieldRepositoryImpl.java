package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.DataField;
import com.hand.hdsp.quality.api.dto.DataFieldDTO;
import com.hand.hdsp.quality.domain.repository.DataFieldRepository;
import org.springframework.stereotype.Component;

/**
 * <p>字段标准表资源库实现</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
@Component
public class DataFieldRepositoryImpl extends BaseRepositoryImpl<DataField, DataFieldDTO> implements DataFieldRepository {

}
