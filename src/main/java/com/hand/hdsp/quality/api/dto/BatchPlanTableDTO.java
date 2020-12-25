package com.hand.hdsp.quality.api.dto;

import java.util.List;
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
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

/**
 * <p>批数据方案-表级规则表 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("批数据方案-表级规则表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@VersionAudit
@ModifyAudit
@ExcelSheet(zh = "表级规则",en = "batch plan table")
public class BatchPlanTableDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long planRuleId;

    @ApiModelProperty(value = "方案-基础配置表XQUA_BATCH_PLAN_BASE.PLAN_BASE_ID")
    @NotNull
    private Long planBaseId;

    @ApiModelProperty(value = "规则编码")
    @NotBlank
    @Size(max = 50)
    @ExcelColumn(zh = "规则编码",en = "rule code")
    private String ruleCode;

    @ApiModelProperty(value = "规则名称")
    @NotBlank
    @Size(max = 255)
    @ExcelColumn(zh = "规则名称",en = "rule name")
    private String ruleName;

    @ApiModelProperty(value = "校验类别 HDSP.XQUA.CHECK_TYPE")
    @ExcelColumn(zh = "评估指标",en = "check type")
    private String checkType;

    @ApiModelProperty(value = "规则类型 HDSP.XQUA.RULE_TYPE")
    @ExcelColumn(zh = "规则类型",en = "rule type")
    private String ruleType;

    @ApiModelProperty(value = "是否异常阻断")
    @NotNull
    @ExcelColumn(zh = "是否异常阻断",en = "exception block")
    private Integer exceptionBlock;

    @ApiModelProperty(value = "权重")
    @ExcelColumn(zh = "权重",en = "weight")
    private Long weight;

    @ApiModelProperty(value = "规则描述")
    @ExcelColumn(zh = "规则描述",en = "rule desc")
    private String ruleDesc;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "表级规则校验项List")
    private List<BatchPlanTableLineDTO> batchPlanTableLineDTOList;

    @ApiModelProperty(value = "校验项 HDSP.XQUA.CHECK_ITEM")
    private String checkItem;

    @ApiModelProperty(value = "校验类型 HDSP.XQUA.COUNT_TYPE")
    private String countType;

    @ApiModelProperty(value = "自定义SQL")
    private String customSql;

    @ApiModelProperty(value = "条件where")
    private String whereCondition;

    @ApiModelProperty(value = "比较方式 HDSP.XQUA.COMPARE_WAY")
    private String compareWay;

    @ApiModelProperty(value = "告警等级json")
    private String warningLevel;

    @ApiModelProperty(value = "方案告警等级List")
    private List<WarningLevelDTO> warningLevelList;
}
