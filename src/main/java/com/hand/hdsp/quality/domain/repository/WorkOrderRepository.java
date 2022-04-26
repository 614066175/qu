package com.hand.hdsp.quality.domain.repository;

import com.hand.hdsp.core.base.repository.BaseRepository;
import com.hand.hdsp.quality.domain.entity.WorkOrder;
import com.hand.hdsp.quality.api.dto.WorkOrderDTO;
import com.hand.hdsp.core.base.ProxySelf;

/**
 * <p>资源库</p>
 *
 * @author guoliang.li01@hand-china.com 2022-04-20 16:28:13
 */
public interface WorkOrderRepository extends BaseRepository<WorkOrder, WorkOrderDTO>, ProxySelf<WorkOrderRepository> {

}