package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.ProxySelf;
import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.api.dto.StandardApproveDTO;
import com.hand.hdsp.quality.domain.entity.StandardApprove;

/**
 * <p>标准申请记录表资源库</p>
 *
 * @author guoliangli01.@hand-china.com 2020-11-25 17:03:12
 */
public interface StandardApproveRepository extends BaseRepository<StandardApprove, StandardApproveDTO>, ProxySelf<StandardApproveRepository> {

}