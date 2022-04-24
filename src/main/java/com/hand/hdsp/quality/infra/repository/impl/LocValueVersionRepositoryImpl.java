package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.LocValueVersionDTO;
import com.hand.hdsp.quality.domain.entity.LocValueVersion;
import com.hand.hdsp.quality.domain.repository.LocValueVersionRepository;
import org.springframework.stereotype.Component;

/**
 * <p>loc独立值集表资源库实现</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-19 16:38:55
 */
@Component
public class LocValueVersionRepositoryImpl extends BaseRepositoryImpl<LocValueVersion, LocValueVersionDTO> implements LocValueVersionRepository {

}
