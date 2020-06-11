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

    @ApiModelProperty(value = "值阈范围")
    private String lovCode;

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

}
