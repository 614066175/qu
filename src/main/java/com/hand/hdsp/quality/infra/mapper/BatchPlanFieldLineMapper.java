package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.RuleLineDTO;
import com.hand.hdsp.quality.domain.entity.BatchPlanFieldLine;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>批数据方案-字段规则校验项表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
public interface BatchPlanFieldLineMapper extends BaseMapper<BatchPlanFieldLine> {

    /**
     * 查询字段规则校验
     *
     * @param checkItemList
     * @param ruleModel
     * @return
     */
    List<RuleLineDTO> list(@Param("checkItemList") List<String> checkItemList, @Param("ruleModel") String ruleModel);

    /**
     * 删除
     *
     * @param planFieldId
     * @return
     */
    @Delete("delete from xqua_batch_plan_field_line where plan_field_id = #{planFieldId}")
    int deleteByParentId(@Param("planFieldId") Long planFieldId);
}
