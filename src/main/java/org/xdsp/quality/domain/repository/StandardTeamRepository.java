package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.StandardTeamDTO;
import org.xdsp.quality.domain.entity.StandardTeam;

/**
 * <p>标准表资源库</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-09 15:50:16
 */
public interface StandardTeamRepository extends BaseRepository<StandardTeam, StandardTeamDTO>, ProxySelf<StandardTeamRepository> {

}