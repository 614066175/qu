package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.WorkOrderOperationDTO;
import org.xdsp.quality.domain.entity.WorkOrderOperation;

/**
 * <p>资源库</p>
 *
 * @author guoliang.li01@hand-china.com 2022-04-20 16:28:13
 */
public interface WorkOrderOperationRepository extends BaseRepository<WorkOrderOperation, WorkOrderOperationDTO>, ProxySelf<WorkOrderOperationRepository> {

}