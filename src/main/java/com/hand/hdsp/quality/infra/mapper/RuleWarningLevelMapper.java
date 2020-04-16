package com.hand.hdsp.quality.infra.mapper;

import com.hand.hdsp.quality.api.dto.RuleWarningLevelDTO;
import com.hand.hdsp.quality.domain.entity.RuleWarningLevel;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>规则告警等级表Mapper</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
public interface RuleWarningLevelMapper extends BaseMapper<RuleWarningLevel> {

    /**
     * 删除
     *
     * @param ruleLineId
     * @return
     */
    @Delete("delete from xqua_rule_warning_level where rule_line_id = #{ruleLineId}")
    int deleteByParentId(@Param("ruleLineId") Long ruleLineId);

    /**
     * 列表
     *
     * @param ruleLineId
     * @param tenantId
     * @return
     */
    @Select("select * from xqua_rule_warning_level where rule_line_id = #{ruleLineId} and tenant_id in(0,#{tenantId})")
    List<RuleWarningLevelDTO> list(@Param("ruleLineId") Long ruleLineId, @Param("tenantId") Long tenantId);
}
