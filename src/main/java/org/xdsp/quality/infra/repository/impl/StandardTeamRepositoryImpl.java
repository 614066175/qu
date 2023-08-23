package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.StandardTeamDTO;
import org.xdsp.quality.domain.entity.StandardTeam;
import org.xdsp.quality.domain.repository.StandardTeamRepository;

/**
 * <p>标准表资源库实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-09 15:50:16
 */
@Component
public class StandardTeamRepositoryImpl extends BaseRepositoryImpl<StandardTeam, StandardTeamDTO> implements StandardTeamRepository {

}
