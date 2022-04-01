package com.hand.hdsp.quality.api.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p>批数据方案结果表-规则信息 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("批数据方案结果表-规则信息")
@JsonInclude(JsonInclude.Include.NON_NULL)
@VersionAudit
@ModifyAudit
public class BatchResultRuleDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long resultRuleId;

    @ApiModelProperty(value = "XQUA_BATCH_RESULT_BASE.RESULT_BASE_ID")
    @NotNull
    private Long resultBaseId;

    @ApiModelProperty(value = "规则类型TABLE/FIELD/REL_TABLE")
    @NotBlank
    @Size(max = 50)
    private String ruleType;

    @ApiModelProperty(value = "方案规则ID(XQUA_BATCH_PLAN_REL_TABLE/XQUA_BATCH_PLAN_FIELD/XQUA_BATCH_PLAN_TABLE表的PLAN_RULE_ID)")
    private Long planRuleId;

    @ApiModelProperty(value = "规则编码")
    private String ruleCode;

    @ApiModelProperty(value = "规则名称")
    private String ruleName;

    @ApiModelProperty(value = "规则描述")
    private String ruleDesc;

    @ApiModelProperty(value = "校验类别 HDSP.XQUA.CHECK_TYPE")
    private String checkType;

    @ApiModelProperty(value = "权重")
    private Long weight;

    @ApiModelProperty(value = "规则结果 1-正常 0-异常")
    @NotNull
    private Integer resultFlag;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    private Long projectId;


}
