package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.StandardTeam;
import com.hand.hdsp.quality.api.dto.StandardTeamDTO;
import com.hand.hdsp.quality.domain.repository.StandardTeamRepository;
import org.springframework.stereotype.Component;

/**
 * <p>标准表资源库实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-09 15:50:16
 */
@Component
public class StandardTeamRepositoryImpl extends BaseRepositoryImpl<StandardTeam, StandardTeamDTO> implements StandardTeamRepository {

}
