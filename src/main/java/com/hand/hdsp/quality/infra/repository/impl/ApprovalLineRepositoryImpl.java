package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.api.dto.ApprovalLineDTO;
import com.hand.hdsp.quality.domain.entity.ApprovalLine;
import com.hand.hdsp.quality.domain.repository.ApprovalLineRepository;
import org.springframework.stereotype.Component;

/**
 * <p>申请行表资源库实现</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-25 20:35:06
 */
@Component
public class ApprovalLineRepositoryImpl extends BaseRepositoryImpl<ApprovalLine, ApprovalLineDTO> implements ApprovalLineRepository {

}
