package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.domain.entity.BatchPlanTable;
import com.hand.hdsp.quality.infra.dataobject.BatchPlanTableDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

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
    int deleteByParentId(@Param("planBaseId") Long planBaseId);
}
