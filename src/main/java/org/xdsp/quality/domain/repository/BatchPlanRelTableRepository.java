package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.BatchPlanRelTableDTO;
import org.xdsp.quality.domain.entity.BatchPlanRelTable;

import java.util.List;

/**
 * <p>批数据方案-表间规则表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface BatchPlanRelTableRepository extends BaseRepository<BatchPlanRelTable, BatchPlanRelTableDTO>, ProxySelf<BatchPlanRelTableRepository> {

    /**
     * 删除
     *
     * @param planBaseId
     * @return
     */
    int deleteByPlanBaseId(Long planBaseId);

    List<BatchPlanRelTableDTO> selectRelTable( BatchPlanRelTableDTO batchPlanRelTableDTO);
}
