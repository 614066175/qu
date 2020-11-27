package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.ExtraVersionDTO;
import com.hand.hdsp.quality.domain.entity.ExtraVersion;
import com.hand.hdsp.quality.domain.repository.ExtraVersionRepository;
import org.springframework.stereotype.Component;

/**
 * <p>附加信息版本表资源库实现</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:45
 */
@Component
public class ExtraVersionRepositoryImpl extends BaseRepositoryImpl<ExtraVersion, ExtraVersionDTO> implements ExtraVersionRepository {

}
