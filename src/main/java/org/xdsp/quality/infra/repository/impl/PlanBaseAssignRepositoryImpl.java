package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.PlanBaseAssignDTO;
import org.xdsp.quality.domain.entity.PlanBaseAssign;
import org.xdsp.quality.domain.repository.PlanBaseAssignRepository;

/**
 * <p>质检项分配表资源库实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-20 15:36:10
 */
@Component
public class PlanBaseAssignRepositoryImpl extends BaseRepositoryImpl<PlanBaseAssign, PlanBaseAssignDTO> implements PlanBaseAssignRepository {

}
