package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.LocDTO;
import org.xdsp.quality.domain.entity.Loc;
import org.xdsp.quality.domain.repository.LocRepository;

/**
 * <p>loc表资源库实现</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Component
public class LocRepositoryImpl extends BaseRepositoryImpl<Loc, LocDTO> implements LocRepository {

}
