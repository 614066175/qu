package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.domain.entity.BatchPlanRelTableLine;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

/**
 * <p>批数据方案-表间规则关联关系表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface BatchPlanRelTableLineMapper extends BaseMapper<BatchPlanRelTableLine> {

    /**
     * 删除
     *
     * @param planRelTableId
     * @return
     */
    @Delete("delete from xqua_batch_plan_rel_table_line where plan_rel_table_id = #{planRelTableId}")
    int deleteByParentId(@Param("planRelTableId") Long planRelTableId);
}
