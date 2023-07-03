package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.StandardOutbibDTO;
import org.xdsp.quality.domain.entity.StandardOutbib;
import org.xdsp.quality.domain.repository.StandardOutbibRepository;

/**
 * <p>标准落标表资源库实现</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-25 17:44:46
 */
@Component
public class StandardOutbibRepositoryImpl extends BaseRepositoryImpl<StandardOutbib, StandardOutbibDTO> implements StandardOutbibRepository {

}
