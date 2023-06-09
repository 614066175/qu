package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.NameAimExcludeDTO;
import org.xdsp.quality.domain.entity.NameAimExclude;
import org.xdsp.quality.domain.repository.NameAimExcludeRepository;

/**
 * <p>落标排除表资源库实现</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Component
public class NameAimExcludeRepositoryImpl extends BaseRepositoryImpl<NameAimExclude, NameAimExcludeDTO> implements NameAimExcludeRepository {

}
