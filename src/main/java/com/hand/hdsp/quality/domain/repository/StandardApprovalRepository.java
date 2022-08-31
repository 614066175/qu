package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.StandardApproval;
import com.hand.hdsp.quality.api.dto.StandardApprovalDTO;
import com.hand.hdsp.core.base.ProxySelf;

/**
 * <p>各种标准审批表资源库</p>
 *
 * @author fuqiang.luo@hand-china.com 2022-08-31 10:30:34
 */
public interface StandardApprovalRepository extends BaseRepository<StandardApproval, StandardApprovalDTO>, ProxySelf<StandardApprovalRepository> {

}