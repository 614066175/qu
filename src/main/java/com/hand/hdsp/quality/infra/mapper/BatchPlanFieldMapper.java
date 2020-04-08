package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.BatchPlanFieldDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanField;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

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
    int deleteByParentId(@Param("planBaseId") Long planBaseId);

    /**
     * 列表
     *
     * @param batchPlanFieldDTO 查询条件
     * @return
     */
    List<BatchPlanFieldDTO> list(BatchPlanFieldDTO batchPlanFieldDTO);
}
