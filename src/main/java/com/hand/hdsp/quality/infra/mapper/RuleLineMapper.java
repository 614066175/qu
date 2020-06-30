package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.RuleLineDTO;
import com.hand.hdsp.quality.domain.entity.RuleLine;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
    @Select("select * from xqua_rule_line where rule_id = #{ruleId} and tenant_id in(0,#{tenantId})")
    List<RuleLineDTO> list(@Param("ruleId") Long ruleId, @Param("tenantId") Long tenantId);

    /**
     * 列表（分页）
     *
     * @param ruleLineDTO
     * @return
     */
    @Select("select * from xqua_rule_line where rule_id = #{ruleId} and tenant_id in(0,#{tenantId})")
    List<RuleLineDTO> listTenant(RuleLineDTO ruleLineDTO);
}
