package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.BatchPlanFieldConDTO;
import org.xdsp.quality.domain.entity.BatchPlanFieldCon;
import org.xdsp.quality.infra.dataobject.BatchPlanFieldConDO;

import java.util.List;

/**
 * <p>批数据方案-字段规则条件表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:03:41
 */
public interface BatchPlanFieldConRepository extends BaseRepository<BatchPlanFieldCon, BatchPlanFieldConDTO>, ProxySelf<BatchPlanFieldConRepository> {

    /**
     * 关联查询校验项
     *
     * @param batchPlanFieldConDO
     * @return
     */
    List<BatchPlanFieldConDO> selectJoinItem(BatchPlanFieldConDO batchPlanFieldConDO);

    /**
     * 根据 planBaseId 删除
     *
     * @param planBaseId
     * @return
     */
    int deleteByPlanBaseId(Long planBaseId);

    /**
     * 根据 plan_line_id 删除
     *
     * @param planLineId
     * @return
     */
    int deleteByPlanLineId(Long planLineId);
}
