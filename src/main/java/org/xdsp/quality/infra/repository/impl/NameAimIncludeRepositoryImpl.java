package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.NameAimIncludeDTO;
import org.xdsp.quality.domain.entity.NameAimInclude;
import org.xdsp.quality.domain.repository.NameAimIncludeRepository;

/**
 * <p>落标包含表资源库实现</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Component
public class NameAimIncludeRepositoryImpl extends BaseRepositoryImpl<NameAimInclude, NameAimIncludeDTO> implements NameAimIncludeRepository {

}
