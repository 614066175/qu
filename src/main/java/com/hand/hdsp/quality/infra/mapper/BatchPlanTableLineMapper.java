package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.domain.entity.BatchPlanTableLine;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

/**
 * <p>批数据方案-表级规则校验项表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
public interface BatchPlanTableLineMapper extends BaseMapper<BatchPlanTableLine> {

    /**
     * 删除
     *
     * @param planRuleId
     * @return
     */
    @Delete("delete from xqua_batch_plan_table_line where plan_rule_id = #{planRuleId}")
    int deleteByPlanRuleId(@Param("planRuleId") Long planRuleId);

    /**
     * 根据 planBaseId 删除
     *
     * @param planBaseId
     * @return
     */
    int deleteByPlanBaseId(Long planBaseId);
}
