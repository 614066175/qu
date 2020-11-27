package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.StandardExtraDTO;
import com.hand.hdsp.quality.domain.entity.StandardExtra;
import com.hand.hdsp.quality.domain.repository.StandardExtraRepository;
import org.springframework.stereotype.Component;

/**
 * <p>标准附加信息表资源库实现</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:45
 */
@Component
public class StandardExtraRepositoryImpl extends BaseRepositoryImpl<StandardExtra, StandardExtraDTO> implements StandardExtraRepository {

}
