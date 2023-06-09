package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.WorkOrderOperationDTO;
import org.xdsp.quality.domain.entity.WorkOrderOperation;
import org.xdsp.quality.domain.repository.WorkOrderOperationRepository;

/**
 * <p>资源库实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-04-20 16:28:13
 */
@Component
public class WorkOrderOperationRepositoryImpl extends BaseRepositoryImpl<WorkOrderOperation, WorkOrderOperationDTO> implements WorkOrderOperationRepository {

}
