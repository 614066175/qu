package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.StandardGroupDTO;
import org.xdsp.quality.domain.entity.StandardGroup;
import org.xdsp.quality.domain.repository.StandardGroupRepository;

/**
 * <p>标准分组表资源库实现</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
@Component
public class StandardGroupRepositoryImpl extends BaseRepositoryImpl<StandardGroup, StandardGroupDTO> implements StandardGroupRepository {

}
