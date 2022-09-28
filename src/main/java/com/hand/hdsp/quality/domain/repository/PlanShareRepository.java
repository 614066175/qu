package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.PlanShareDTO;
import com.hand.hdsp.quality.domain.entity.PlanShare;

/**
 * <p>资源库</p>
 *
 * @author guoliang.li01@hand-china.com 2022-09-26 14:04:11
 */
public interface PlanShareRepository extends BaseRepository<PlanShare, PlanShareDTO>, ProxySelf<PlanShareRepository> {

}