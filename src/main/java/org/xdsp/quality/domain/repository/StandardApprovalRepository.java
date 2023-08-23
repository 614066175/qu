package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.StandardApprovalDTO;
import org.xdsp.quality.domain.entity.StandardApproval;

/**
 * <p>各种标准审批表资源库</p>
 *
 * @author fuqiang.luo@hand-china.com 2022-08-31 10:30:34
 */
public interface StandardApprovalRepository extends BaseRepository<StandardApproval, StandardApprovalDTO>, ProxySelf<StandardApprovalRepository> {

}