package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.StandardAimDTO;
import org.xdsp.quality.domain.entity.StandardAim;
import org.xdsp.quality.domain.repository.StandardAimRepository;

/**
 * <p>标准落标表资源库实现</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-30 10:23:51
 */
@Component
public class StandardAimRepositoryImpl extends BaseRepositoryImpl<StandardAim, StandardAimDTO> implements StandardAimRepository {

}
