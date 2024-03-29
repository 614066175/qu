package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.xdsp.quality.api.dto.BatchPlanFieldDTO;
import org.xdsp.quality.domain.entity.BatchPlanField;

import java.util.List;

/**
 * <p>批数据方案-字段规则表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface BatchPlanFieldMapper extends BaseMapper<BatchPlanField> {

    /**
     * 删除
     *
     * @param planBaseId
     * @return
     */
    @Delete("delete from xqua_batch_plan_field where plan_base_id = #{planBaseId}")
    int deleteByPlanBaseId(@Param("planBaseId") Long planBaseId);

    /**
     * 列表
     *
     * @param batchPlanFieldDTO 查询条件
     * @return
     */
    List<BatchPlanFieldDTO> selectList(BatchPlanFieldDTO batchPlanFieldDTO);

    /**
     * 列表
     *
     * @param batchPlanField 查询条件
     * @return
     */
    List<BatchPlanField> list(BatchPlanField batchPlanField);

    /**
     * 字段规则-规则详情查询方法
     *
     * @param batchPlanFieldDTO
     * @return
     */
    List<BatchPlanFieldDTO> selectDetailList(BatchPlanFieldDTO batchPlanFieldDTO);

    /**
     * 获取导出字段级规则，包含检验项和配置项
     *
     * @param planBaseId
     * @return
     */
    List<BatchPlanFieldDTO> getPlanField(Long planBaseId);
}
