package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.xdsp.quality.api.dto.BatchPlanTableDTO;
import org.xdsp.quality.domain.entity.BatchPlanTable;
import org.xdsp.quality.infra.dataobject.BatchPlanTableDO;

import java.util.List;

/**
 * <p>批数据方案-表级规则表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
public interface BatchPlanTableMapper extends BaseMapper<BatchPlanTable> {

    /**
     * 表级规则列表
     *
     * @param batchPlanTableDO 查询条件
     * @return
     */
    List<BatchPlanTableDO> list(BatchPlanTableDO batchPlanTableDO);

    /**
     * 删除
     *
     * @param planBaseId
     * @return
     */
    @Delete("delete from xqua_batch_plan_table where plan_base_id = #{planBaseId}")
    int deleteByPlanBaseId(@Param("planBaseId") Long planBaseId);

    /**
     * 表级规则-规则详情查询方法
     *
     * @param batchPlanTableDTO
     * @return
     */
    List<BatchPlanTableDTO> selectDetailList(BatchPlanTableDTO batchPlanTableDTO);

    /**
     * 根据质检项id获取编辑规则
     *
     * @param planBaseId
     * @return
     */
    List<BatchPlanTableDTO> getPlanTable(Long planBaseId);

    List<BatchPlanTableDTO> selectPlanTable(BatchPlanTableDTO batchPlanTableDTO);

}
