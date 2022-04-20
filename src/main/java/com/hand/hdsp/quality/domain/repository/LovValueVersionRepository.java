package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.CodeVersion;
import com.hand.hdsp.quality.api.dto.LovValueVersionDTO;
import com.hand.hdsp.quality.domain.entity.LovValueVersion;

import java.util.List;

/**
 * <p>LOV独立值集表资源库</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-19 16:38:55
 */
public interface LovValueVersionRepository extends BaseRepository<LovValueVersion, LovValueVersionDTO>, ProxySelf<LovValueVersionRepository> {


}