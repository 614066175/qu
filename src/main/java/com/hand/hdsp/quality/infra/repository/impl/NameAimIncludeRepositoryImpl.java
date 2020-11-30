package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.NameAimInclude;
import com.hand.hdsp.quality.api.dto.NameAimIncludeDTO;
import com.hand.hdsp.quality.domain.repository.NameAimIncludeRepository;
import org.springframework.stereotype.Component;

/**
 * <p>落标包含表资源库实现</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Component
public class NameAimIncludeRepositoryImpl extends BaseRepositoryImpl<NameAimInclude, NameAimIncludeDTO> implements NameAimIncludeRepository {

}
