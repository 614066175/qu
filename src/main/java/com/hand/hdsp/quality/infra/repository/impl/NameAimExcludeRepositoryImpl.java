package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.NameAimExclude;
import com.hand.hdsp.quality.api.dto.NameAimExcludeDTO;
import com.hand.hdsp.quality.domain.repository.NameAimExcludeRepository;
import org.springframework.stereotype.Component;

/**
 * <p>落标排除表资源库实现</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Component
public class NameAimExcludeRepositoryImpl extends BaseRepositoryImpl<NameAimExclude, NameAimExcludeDTO> implements NameAimExcludeRepository {

}
