package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.PlanShareDTO;
import org.xdsp.quality.domain.entity.PlanShare;
import org.xdsp.quality.domain.repository.PlanShareRepository;

/**
 * <p>资源库实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-09-26 14:04:11
 */
@Component
public class PlanShareRepositoryImpl extends BaseRepositoryImpl<PlanShare, PlanShareDTO> implements PlanShareRepository {

}
