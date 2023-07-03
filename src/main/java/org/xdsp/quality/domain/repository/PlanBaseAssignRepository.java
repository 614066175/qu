package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.PlanBaseAssignDTO;
import org.xdsp.quality.domain.entity.PlanBaseAssign;

/**
 * <p>质检项分配表资源库</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-20 15:36:10
 */
public interface PlanBaseAssignRepository extends BaseRepository<PlanBaseAssign, PlanBaseAssignDTO>, ProxySelf<PlanBaseAssignRepository> {

}