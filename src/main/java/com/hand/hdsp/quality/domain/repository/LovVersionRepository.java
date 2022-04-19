package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.LovVersionDTO;
import com.hand.hdsp.quality.domain.entity.LovVersion;

/**
 * <p>LOV表资源库</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LovVersionRepository extends BaseRepository<LovVersion, LovVersionDTO>, ProxySelf<LovVersionRepository> {

}