package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.NameAim;
import com.hand.hdsp.quality.api.dto.NameAimDTO;
import com.hand.hdsp.quality.domain.repository.NameAimRepository;
import org.springframework.stereotype.Component;

/**
 * <p>命名落标表资源库实现</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Component
public class NameAimRepositoryImpl extends BaseRepositoryImpl<NameAim, NameAimDTO> implements NameAimRepository {

}
