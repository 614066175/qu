package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.LocDTO;
import com.hand.hdsp.quality.domain.entity.Loc;

/**
 * <p>loc表资源库</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LocRepository extends BaseRepository<Loc, LocDTO>, ProxySelf<LocRepository> {

}