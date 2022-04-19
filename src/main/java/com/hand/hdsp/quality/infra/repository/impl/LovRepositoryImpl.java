package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.LovDTO;
import com.hand.hdsp.quality.domain.entity.Lov;
import com.hand.hdsp.quality.domain.repository.LovRepository;
import org.springframework.stereotype.Component;

/**
 * <p>LOV表资源库实现</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Component
public class LovRepositoryImpl extends BaseRepositoryImpl<Lov, LovDTO> implements LovRepository {

}
