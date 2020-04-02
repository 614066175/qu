package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.domain.entity.StreamingPlanRule;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

/**
 * <p>实时数据方案-规则表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
public interface StreamingPlanRuleMapper extends BaseMapper<StreamingPlanRule> {

    /**
     * 删除
     *
     * @param planBaseId
     * @return
     */
    @Delete("delete from xqua_streaming_plan_rule where plan_base_id = #{planBaseId}")
    int deleteByParentId(@Param("planBaseId") Long planBaseId);
}
