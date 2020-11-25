package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.StandardGroupDTO;
import com.hand.hdsp.quality.domain.entity.StandardGroup;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/23 21:05
 * @since 1.0
 */
public interface StandardGroupRepository extends BaseRepository<StandardGroup, StandardGroupDTO>, ProxySelf<StandardGroupRepository> {

}
