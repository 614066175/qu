package org.xdsp.quality.infra.repository.impl;

import org.springframework.stereotype.Component;
import org.xdsp.core.base.repository.impl.BaseRepositoryImpl;
import org.xdsp.quality.api.dto.WorkOrderDTO;
import org.xdsp.quality.domain.entity.WorkOrder;
import org.xdsp.quality.domain.repository.WorkOrderRepository;

/**
 * <p>资源库实现</p>
 *
 * @author guoliang.li01@hand-china.com 2022-04-20 16:28:13
 */
@Component
public class WorkOrderRepositoryImpl extends BaseRepositoryImpl<WorkOrder, WorkOrderDTO> implements WorkOrderRepository {

}
