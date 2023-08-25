package org.xdsp.quality.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.xdsp.quality.api.dto.RuleLineDTO;
import org.xdsp.quality.domain.entity.RuleLine;

import java.util.List;

/**
 * <p>规则校验项表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
public interface RuleLineMapper extends BaseMapper<RuleLine> {

    /**
     * 删除
     *
     * @param ruleId
     * @return
     */
    @Delete("delete from xqua_rule_line where rule_id = #{ruleId}")
    int deleteByParentId(@Param("ruleId") Long ruleId);

    /**
     * 列表
     *
     * @param ruleId
     * @param tenantId
     * @return
     */
    @Select("select * from xqua_rule_line where rule_id = #{ruleId} and tenant_id in(0,#{tenantId}) and project_id = #{projectId}")
    List<RuleLineDTO> list(@Param("ruleId") Long ruleId, @Param("tenantId") Long tenantId,@Param("projectId") Long projectId);

    /**
     * 列表（分页）
     *
     * @param ruleLineDTO
     * @return
     */
    @Select("select * from xqua_rule_line where rule_id = #{ruleId} and tenant_id in(0,#{tenantId}) and project_id = #{projectId}")
    List<RuleLineDTO> listTenant(RuleLineDTO ruleLineDTO);
}
