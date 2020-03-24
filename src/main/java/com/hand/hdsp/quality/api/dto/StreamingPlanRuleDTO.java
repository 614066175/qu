package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * <p>实时数据方案-规则表 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("实时数据方案-规则表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StreamingPlanRuleDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long planRuleId;

    @ApiModelProperty(value = "实时数据方案-基础配置表XQUA_STREAMING_PLAN_BASE.PLAN_BASE_ID")
    @NotNull
    private Long planBaseId;

    @ApiModelProperty(value = "规则编码")
    @NotBlank
    @Size(max = 50)
    private String ruleCode;

    @ApiModelProperty(value = "规则名称")
    @NotBlank
    @Size(max = 255)
    private String ruleName;

    @ApiModelProperty(value = "规则描述")
    private String ruleDesc;

    @ApiModelProperty(value = "规则类型 HDSP.XQUA.STREAMING.RULE_TYPE")
    private String ruleType;

    @ApiModelProperty(value = "权重")
    private Long weight;

    @ApiModelProperty(value = "告警记录数阈值")
    private Long warningCount;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

}
