package org.xdsp.quality.domain.repository;

import org.xdsp.core.base.ProxySelf;
import org.xdsp.core.base.repository.BaseRepository;
import org.xdsp.quality.api.dto.BatchPlanTableDTO;
import org.xdsp.quality.domain.entity.BatchPlanTable;
import org.xdsp.quality.infra.dataobject.BatchPlanTableDO;

import java.util.List;

/**
 * <p>批数据方案-表级规则表资源库</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
public interface BatchPlanTableRepository extends BaseRepository<BatchPlanTable, BatchPlanTableDTO>, ProxySelf<BatchPlanTableRepository> {

    /**
     * 表级规则
     *
     * @param batchPlanTableDO
     * @return
     */
    List<BatchPlanTableDO> list(BatchPlanTableDO batchPlanTableDO);

    /**
     * 删除
     *
     * @param planBaseId
     * @return
     */
    int deleteByPlanBaseId(Long planBaseId);

    /**
     * 表级规则-规则详情查询方法
     *
     * @param batchPlanTableDTO
     * @return
     */
    List<BatchPlanTableDTO> selectDetailList(BatchPlanTableDTO batchPlanTableDTO);

    List<BatchPlanTableDTO> selectTableList(BatchPlanTableDTO batchPlanTableDTO);

}
