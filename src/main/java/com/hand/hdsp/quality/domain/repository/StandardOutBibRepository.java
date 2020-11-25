package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.StandardOutBibDTO;
import com.hand.hdsp.quality.domain.entity.StandardOutBib;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/25 10:46
 * @since 1.0
 */
public interface StandardOutBibRepository extends BaseRepository<StandardOutBib, StandardOutBibDTO> , ProxySelf<StandardOutBibRepository> {

}
