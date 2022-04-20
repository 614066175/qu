package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.CodeVersion;
import com.hand.hdsp.quality.api.dto.LovValueVersionDTO;
import com.hand.hdsp.quality.domain.entity.LovValueVersion;
import com.hand.hdsp.quality.domain.repository.LovValueVersionRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>LOV独立值集表资源库实现</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-19 16:38:55
 */
@Component
public class LovValueVersionRepositoryImpl extends BaseRepositoryImpl<LovValueVersion, LovValueVersionDTO> implements LovValueVersionRepository {

}
