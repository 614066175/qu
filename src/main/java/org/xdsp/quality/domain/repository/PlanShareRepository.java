package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.PlanShareDTO;
import org.xdsp.quality.domain.entity.PlanShare;

/**
 * <p>资源库</p>
 *
 * @author guoliang.li01@hand-china.com 2022-09-26 14:04:11
 */
public interface PlanShareRepository extends BaseRepository<PlanShare, PlanShareDTO>, ProxySelf<PlanShareRepository> {

}