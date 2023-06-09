package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.StandardExtraDTO;
import org.xdsp.quality.domain.entity.StandardExtra;
import org.xdsp.quality.domain.repository.StandardExtraRepository;

/**
 * <p>标准附加信息表资源库实现</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:45
 */
@Component
public class StandardExtraRepositoryImpl extends BaseRepositoryImpl<StandardExtra, StandardExtraDTO> implements StandardExtraRepository {

}
