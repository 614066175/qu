package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StandardAimDTO;
import com.hand.hdsp.quality.domain.entity.StandardAim;
import com.hand.hdsp.quality.domain.repository.StandardAimRepository;
import org.springframework.stereotype.Component;

/**
 * <p>标准落标表资源库实现</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
@Component
public class StandardAimRepositoryImpl extends BaseRepositoryImpl<StandardAim, StandardAimDTO> implements StandardAimRepository {

}
