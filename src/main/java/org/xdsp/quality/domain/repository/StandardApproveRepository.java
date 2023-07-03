package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.StandardApproveDTO;
import org.xdsp.quality.domain.entity.StandardApprove;

/**
 * <p>标准申请记录表资源库</p>
 *
 * @author guoliangli01.@hand-china.com 2020-11-25 17:03:12
 */
public interface StandardApproveRepository extends BaseRepository<StandardApprove, StandardApproveDTO>, ProxySelf<StandardApproveRepository> {

}