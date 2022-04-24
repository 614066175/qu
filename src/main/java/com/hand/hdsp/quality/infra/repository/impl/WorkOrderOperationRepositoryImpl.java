package com.hand.hdsp.quality.infra.repository.impl;

import com.hand.hdsp.core.base.repository.impl.BaseRepositoryImpl;
import com.hand.hdsp.quality.domain.entity.WorkOrderOperation;
import com.hand.hdsp.quality.api.dto.WorkOrderOperationDTO;
import com.hand.hdsp.quality.domain.repository.WorkOrderOperationRepository;
import org.springframework.stereotype.Component;

/**
 * <p>资源库实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-04-20 16:28:13
 */
@Component
public class WorkOrderOperationRepositoryImpl extends BaseRepositoryImpl<WorkOrderOperation, WorkOrderOperationDTO> implements WorkOrderOperationRepository {

}