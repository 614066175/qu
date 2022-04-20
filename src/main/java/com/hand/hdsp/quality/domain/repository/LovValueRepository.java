package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.LovValueDTO;
import com.hand.hdsp.quality.domain.entity.LovValue;

import java.util.List;

/**
 * <p>LOV独立值集表资源库</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
public interface LovValueRepository extends BaseRepository<LovValue, LovValueDTO>, ProxySelf<LovValueRepository> {

    List<LovValueDTO> query(Long lovId, String query);
}