package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.LocDTO;
import com.hand.hdsp.quality.domain.entity.Loc;
import com.hand.hdsp.quality.domain.repository.LocRepository;
import org.springframework.stereotype.Component;

/**
 * <p>loc表资源库实现</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Component
public class LocRepositoryImpl extends BaseRepositoryImpl<Loc, LocDTO> implements LocRepository {

}
