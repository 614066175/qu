package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.PlanShareDTO;
import com.hand.hdsp.quality.domain.entity.PlanShare;
import com.hand.hdsp.quality.domain.repository.PlanShareRepository;
import org.springframework.stereotype.Component;

/**
 * <p>资源库实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-09-26 14:04:11
 */
@Component
public class PlanShareRepositoryImpl extends BaseRepositoryImpl<PlanShare, PlanShareDTO> implements PlanShareRepository {

}
