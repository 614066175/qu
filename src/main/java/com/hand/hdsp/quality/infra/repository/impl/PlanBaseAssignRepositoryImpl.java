package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.PlanBaseAssignDTO;
import com.hand.hdsp.quality.domain.entity.PlanBaseAssign;
import com.hand.hdsp.quality.domain.repository.PlanBaseAssignRepository;
import org.springframework.stereotype.Component;

/**
 * <p>质检项分配表资源库实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-20 15:36:10
 */
@Component
public class PlanBaseAssignRepositoryImpl extends BaseRepositoryImpl<PlanBaseAssign, PlanBaseAssignDTO> implements PlanBaseAssignRepository {

}
