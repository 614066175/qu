package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.StandardApprovalDTO;
import org.xdsp.quality.domain.entity.StandardApproval;
import org.xdsp.quality.domain.repository.StandardApprovalRepository;

/**
 * <p>各种标准审批表资源库实现</p>
 *
 * @author fuqiang.luo@hand-china.com 2022-08-31 10:30:34
 */
@Component
public class StandardApprovalRepositoryImpl extends BaseRepositoryImpl<StandardApproval, StandardApprovalDTO> implements StandardApprovalRepository {

}
