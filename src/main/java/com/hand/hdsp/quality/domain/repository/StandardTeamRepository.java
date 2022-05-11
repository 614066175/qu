package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.StandardTeam;
import com.hand.hdsp.quality.api.dto.StandardTeamDTO;
import com.hand.hdsp.core.base.ProxySelf;

/**
 * <p>标准表资源库</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-09 15:50:16
 */
public interface StandardTeamRepository extends BaseRepository<StandardTeam, StandardTeamDTO>, ProxySelf<StandardTeamRepository> {

}