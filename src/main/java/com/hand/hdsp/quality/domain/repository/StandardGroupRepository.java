package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.StandardGroup;

/**
 * <p>标准分组表资源库</p>
 *
 * @author guoliangli01.@hand-china.com 2020-11-25 17:03:12
 */
public interface StandardGroupRepository extends BaseRepository<StandardGroup, StandardGroupDTO>, ProxySelf<StandardGroupRepository> {

}