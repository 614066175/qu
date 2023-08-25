package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.ExtraVersionDTO;
import org.xdsp.quality.domain.entity.ExtraVersion;
import org.xdsp.quality.domain.repository.ExtraVersionRepository;

/**
 * <p>附加信息版本表资源库实现</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:45
 */
@Component
public class ExtraVersionRepositoryImpl extends BaseRepositoryImpl<ExtraVersion, ExtraVersionDTO> implements ExtraVersionRepository {

}
