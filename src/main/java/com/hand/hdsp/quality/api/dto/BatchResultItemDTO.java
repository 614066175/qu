package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * <p>批数据方案结果表-校验项信息 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:28:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("批数据方案结果表-校验项信息")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchResultItemDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long resultItemId;

    @ApiModelProperty(value = "规则结果ID XQUA_BATCH_RESULT_RULE.RESULT_RULE_ID")
    @NotNull
    private Long resultRuleId;

    @ApiModelProperty(value = "XQUA_BATCH_PLAN_TABLE_LINE.PLAN_LINE_ID/XQUA_BATCH_PLAN_FIELD_LINE.PLAN_LINE_ID")
    private Long planLineId;

    @ApiModelProperty(value = "XQUA_BATCH_PLAN_TABLE_CON.CONDITION_ID/XQUA_BATCH_PLAN_FIELD_CON.CONDITION_ID")
    private Long conditionId;

    @ApiModelProperty(value = "校验方式 HDSP.XQUA.CHECK_WAY")
    private String checkWay;

    @ApiModelProperty(value = "校验项 HDSP.XQUA.CHECK_ITEM")
    private String checkItem;

    @ApiModelProperty(value = "校验类型 HDSP.XQUA.COUNT_TYPE")
    private String countType;

    @ApiModelProperty(value = "规则字段，多个列逗号拼接")
    @NotBlank
    @Size(max = 1000)
    private String fieldName;

    @ApiModelProperty(value = "校验字段，多个列逗号拼接")
    @NotBlank
    @Size(max = 1000)
    private String checkFieldName;

    @ApiModelProperty(value = "条件where")
    private String whereCondition;

    @ApiModelProperty(value = "比较方式 HDSP.XQUA.COMPARE_WAY")
    private String compareWay;

    @ApiModelProperty(value = "正则表达式")
    private String regularExpression;

    @ApiModelProperty(value = "自定义SQL")
    private String customSql;

    @ApiModelProperty(value = "关联数据源类型(快码：HDSP.DATASOURCE_TYPE)")
    private String relDatasourceType;

    @ApiModelProperty(value = "关联数据源ID")
    private Long relDatasourceId;

    @ApiModelProperty(value = "关联数据库")
    private String relSchema;

    @ApiModelProperty(value = "关联表名")
    private String relTableName;

    @ApiModelProperty(value = "关联关系json")
    private String relationship;

    @ApiModelProperty(value = "告警等级json")
    private String warningLevelJson;

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

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "权重")
    private Long weight;

    @ApiModelProperty(value = "规则类型TABLE/FIELD/REL_TABLE")
    private String ruleType;

    @ApiModelProperty(value = "规则编码")
    private String ruleCode;

    @ApiModelProperty(value = "规则名称")
    private String ruleName;

    @ApiModelProperty(value = "规则描述")
    private String ruleDesc;

    @ApiModelProperty(value = "校验类别 HDSP.XQUA.CHECK_TYPE")
    private String checkType;

    @ApiModelProperty(value = "批数据方案结果表XQUA_BATCH_RESULT.RESULT_ID")
    private Long resultId;

    @ApiModelProperty(value = "XQUA_BATCH_RESULT_BASE.RESULT_BASE_ID")
    private Long resultBaseId;

    @ApiModelProperty(value = "方案告警等级List")
    private List<WarningLevelDTO> warningLevelList;

    @ApiModelProperty(value = "表名/视图名/自定义SQL")
    private String objectName;
}
