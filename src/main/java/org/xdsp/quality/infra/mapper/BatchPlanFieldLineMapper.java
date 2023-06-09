package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.xdsp.quality.domain.entity.BatchPlanFieldLine;

/**
 * <p>批数据方案-字段规则校验项表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface BatchPlanFieldLineMapper extends BaseMapper<BatchPlanFieldLine> {

    /**
     * 删除
     *
     * @param planRuleId
     * @return
     */
    @Delete("delete from xqua_batch_plan_field_line where plan_rule_id = #{planRuleId}")
    int deleteByParentId(@Param("planRuleId") Long planRuleId);

    /**
     * 根据 planBaseId 删除
     *
     * @param planBaseId
     * @return
     */
    int deleteByPlanBaseId(Long planBaseId);
}
