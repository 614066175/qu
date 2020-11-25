package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.StandardOutbibDTO;
import com.hand.hdsp.quality.domain.entity.StandardOutbib;

/**
 * <p>标准落标表资源库</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-25 17:44:46
 */
public interface StandardOutbibRepository extends BaseRepository<StandardOutbib, StandardOutbibDTO>, ProxySelf<StandardOutbibRepository> {

}