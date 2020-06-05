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

    @ApiModelProperty(value = "校验表名称")
    private String tableName;

    @ApiModelProperty(value = "方案规则ID(XQUA_BATCH_PLAN_REL_TABLE.PLAN_RULE_ID/XQUA_BATCH_PLAN_FIELD.PLAN_RULE_ID/XQUA_BATCH_PLAN_TABLE.PLAN_RULE_ID)结合规则类型判断具体是哪个")
    private Long ruleId;

    @ApiModelProperty(value = "XQUA_BATCH_PLAN_FIELD_LINE.PLAN_LINE_ID")
    private Long planLineId;

    @ApiModelProperty(value = "规则编码")
    private String ruleCode;

    @ApiModelProperty(value = "规则名称")
    private String ruleName;

    @ApiModelProperty(value = "校验项 HDSP.XQUA.CHECK_ITEM")
    private String checkItem;

    @ApiModelProperty(value = "比较方式 HDSP.XQUA.COMPARE_WAY")
    private String compareWay;

    @ApiModelProperty(value = "阈值")
    private String expectedValue;

    @ApiModelProperty(value = "实际值")
    private String actualValue;

    @ApiModelProperty(value = "波动率")
    private String waveRate;

    @ApiModelProperty(value = "当前值（用于字段规则计算波动率）")
    private String currentValue;

    @ApiModelProperty(value = "错误信息")
    private String exceptionInfo;

    @ApiModelProperty(value = "告警等级 HDSP.XQUA.WARNING_LEVEL")
    private String warningLevel;

    @ApiModelProperty(value = "列名")
    private String fieldName;

    @ApiModelProperty(value = "对比表名")
    private String relTableName;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "权重")
    private Long weight;
}
