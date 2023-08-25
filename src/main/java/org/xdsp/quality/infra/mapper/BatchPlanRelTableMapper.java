package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.xdsp.quality.api.dto.BatchPlanRelTableDTO;
import org.xdsp.quality.domain.entity.BatchPlanRelTable;

import java.util.List;

/**
 * <p>批数据方案-表间规则表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface BatchPlanRelTableMapper extends BaseMapper<BatchPlanRelTable> {

    /**
     * 删除
     *
     * @param planBaseId
     * @return
     */
    @Delete("delete from xqua_batch_plan_rel_table where plan_base_id = #{planBaseId}")
    int deleteByPlanBaseId(@Param("planBaseId") Long planBaseId);

    BatchPlanRelTableDTO selectDatasourceIdAndType(@Param("batchPlanRelTableDTO") BatchPlanRelTableDTO batchPlanRelTableDTO);

    /**
     * 根据质检项id获取表间规则
     *
     * @param planBaseId
     * @return
     */
    List<BatchPlanRelTableDTO> getRelTable(Long planBaseId);
    List<BatchPlanRelTableDTO> selectRelTable(BatchPlanRelTableDTO batchPlanRelTableDTO);
}
