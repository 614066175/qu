package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.StandardExtraDTO;
import com.hand.hdsp.quality.domain.entity.StandardExtra;

/**
 * <p>标准额外信息表资源库</p>
 *
 * @author guoliangli01.@hand-china.com 2020-11-25 17:03:11
 */
public interface StandardExtraRepository extends BaseRepository<StandardExtra, StandardExtraDTO>, ProxySelf<StandardExtraRepository> {

}