package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hand.hdsp.quality.infra.render.BooleanColumnRender;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * <p>批数据方案-字段规则表 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("批数据方案-字段规则表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@VersionAudit
@ModifyAudit
@ExcelSheet(zh = "字段级规则", en = "batch plan field", rowOffset = 2)
public class BatchPlanFieldDTO extends AuditDomain {

    //=======导出字段==========
    @ApiModelProperty(value = "质检项编码")
    @ExcelColumn(zh = "质检项编码", en = "plan base code")
    @Transient
    private String planBaseCode;

    @ApiModelProperty(value = "规则编码")
    @NotBlank
    @Size(max = 50)
    @ExcelColumn(zh = "规则编码", en = "rule code")
    private String ruleCode;

    @ApiModelProperty(value = "规则名称")
    @NotBlank
    @Size(max = 255)
    @ExcelColumn(zh = "规则名称", en = "rule name")
    private String ruleName;

    @ApiModelProperty(value = "评估指标 HDSP.XQUA.CHECK_TYPE")
    @ExcelColumn(zh = "评估指标", en = "check type")
    private String checkType;


    @ApiModelProperty(value = "是否异常阻断")
    @NotNull
    @ExcelColumn(zh = "是否异常阻断", en = "exception block", renderers = BooleanColumnRender.class)
    private Integer exceptionBlock;

    @ApiModelProperty(value = "权重")
    @ExcelColumn(zh = "权重", en = "weight")
    private Long weight;

    @ApiModelProperty(value = "规则描述")
    @ExcelColumn(zh = "规则描述", en = "rule desc")
    private String ruleDesc;

    @ApiModelProperty(value = "字段规则校验项List")
    private List<BatchPlanFieldLineDTO> batchPlanFieldLineDTOList;


    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long planRuleId;

    @ApiModelProperty(value = "方案-基础配置表XQUA_BATCH_PLAN_BASE.PLAN_BASE_ID")
    @NotNull
    private Long planBaseId;


    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "校验方式 HDSP.XQUA.CHECK_WAY")
    @ExcelColumn(zh = "校验方式", en = "check way")
    private String checkWay;

    @ApiModelProperty(value = "校验项 HDSP.XQUA.CHECK_ITEM")
    @ExcelColumn(zh = "校验项", en = "check item")
    private String checkItem;

    @ApiModelProperty(value = "校验类型 HDSP.XQUA.COUNT_TYPE")
    @ExcelColumn(zh = "校验类型", en = "count type")
    private String countType;

    @ApiModelProperty(value = "规则字段，多个列逗号拼接")
    @ExcelColumn(zh = "规则字段", en = "field name")
    private String fieldName;

    @ApiModelProperty(value = "校验字段，多个列逗号拼接")
    @ExcelColumn(zh = "校验字段", en = "check field name")
    private String checkFieldName;

    @ApiModelProperty(value = "维度字段，多个列逗号拼接")
    @ExcelColumn(zh = "维度字段", en = "dimension field")
    private String dimensionField;

    @ApiModelProperty(value = "正则表达式")
    @ExcelColumn(zh = "正则表达式", en = "regularExpression")
    private String regularExpression;


    @ApiModelProperty(value = "条件where")
    @ExcelColumn(zh = "启用条件", en = "whereCondition")
    private String whereCondition;

    @ApiModelProperty(value = "比较方式 HDSP.XQUA.COMPARE_WAY")
    @ExcelColumn(zh = "比较方式", en = "compareWay")
    private String compareWay;

    @ApiModelProperty(value = "告警等级json")
    @ExcelColumn(zh = "告警规则", en = "warningLevel")
    private String warningLevel;

    @ApiModelProperty(value = "方案告警等级List")
    private List<WarningLevelDTO> warningLevelList;

    private String datasourceName;

    private String planName;

    private String objectName;

    private String datasourceSchema;

    private String ruleType;

    private String realName;

    @Transient
    private String exceptionBlockFlag;

    private Long projectId;

    @ApiModelProperty(value = "是否自动生成 0 否 | 1 是")
    private Integer autoBuildFlag;
}
