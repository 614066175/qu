package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.StandardApproval;
import com.hand.hdsp.quality.api.dto.StandardApprovalDTO;
import com.hand.hdsp.quality.domain.repository.StandardApprovalRepository;
import org.springframework.stereotype.Component;

/**
 * <p>各种标准审批表资源库实现</p>
 *
 * @author fuqiang.luo@hand-china.com 2022-08-31 10:30:34
 */
@Component
public class StandardApprovalRepositoryImpl extends BaseRepositoryImpl<StandardApproval, StandardApprovalDTO> implements StandardApprovalRepository {

}
