package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.NameStandard;
import com.hand.hdsp.quality.api.dto.NameStandardDTO;
import com.hand.hdsp.quality.domain.repository.NameStandardRepository;
import org.springframework.stereotype.Component;

/**
 * <p>命名标准表资源库实现</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@Component
public class NameStandardRepositoryImpl extends BaseRepositoryImpl<NameStandard, NameStandardDTO> implements NameStandardRepository {

}
