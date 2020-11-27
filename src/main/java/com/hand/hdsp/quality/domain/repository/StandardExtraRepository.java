package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.StandardExtraDTO;
import com.hand.hdsp.quality.domain.entity.StandardExtra;

/**
 * <p>标准附加信息表资源库</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:45
 */
public interface StandardExtraRepository extends BaseRepository<StandardExtra, StandardExtraDTO>, ProxySelf<StandardExtraRepository> {

}