package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.PlanBaseAssignDTO;
import com.hand.hdsp.quality.domain.entity.PlanBaseAssign;

/**
 * <p>质检项分配表资源库</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-20 15:36:10
 */
public interface PlanBaseAssignRepository extends BaseRepository<PlanBaseAssign, PlanBaseAssignDTO>, ProxySelf<PlanBaseAssignRepository> {

}