package com.hand.hdsp.quality.api.dto;

import java.util.List;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

/**
 * <p>批数据方案-表间规则表 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("批数据方案-表间规则表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@VersionAudit
@ModifyAudit
@ExcelSheet(zh = "表间规则",en = "batch plan rel table")
public class BatchPlanRelTableDTO extends AuditDomain {

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

    @ApiModelProperty(value = "规则描述")
    @ExcelColumn(zh = "规则描述",en = "rule desc")
    private String ruleDesc;

    @ApiModelProperty(value = "校验类别 HDSP.XQUA.CHECK_TYPE")
    @ExcelColumn(zh = "校验类型",en = "check type")
    private String checkType;

    @ApiModelProperty(value = "是否异常阻断")
    @NotNull
    @ExcelColumn(zh = "是否异常阻断",en = "exception block" , renderers = BooleanColumnRender.class)
    private Integer exceptionBlock;

    @ApiModelProperty(value = "权重")
    @ExcelColumn(zh = "权重",en = "weight")
    private Long weight;

    @ApiModelProperty(value = "校验项")
    @ExcelColumn(zh = "检验项",en = "checkItem")
    private String checkItem;

    @ApiModelProperty(value = "关联数据源类型(快码：HDSP.DATASOURCE_TYPE)")
    private String relDatasourceType;

    @ApiModelProperty(value = "关联数据源ID")
    private Long relDatasourceId;

    @ApiModelProperty(value = "关联数据库")
    @ExcelColumn(zh = "schema",en = "rel schema")
    private String relSchema;

    @ApiModelProperty(value = "关联表名")
    @ExcelColumn(zh = "表名",en = "table name")
    private String relTableName;

    @ApiModelProperty(value = "关联表条件where")
    private String whereCondition;

    @ApiModelProperty(value = "关联关系json")
    private String relationship;

    @ApiModelProperty(value = "表间校验json")
    private String tableRelCheck;

    @ApiModelProperty(value = "告警等级json")
    private String warningLevel;

    @ApiModelProperty(value = "关联关系list")
    private List<RelationshipDTO> relationshipList;

    @ApiModelProperty(value = "告警等级list")
    private List<WarningLevelDTO> warningLevelList;

    @ApiModelProperty(value = "表间校验list")
    @Transient
    private List<TableRelCheckDTO> tableRelCheckList;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    private String datasourceCode;

    @Transient
    private String exceptionBlockFlag;

    @Transient
    private String planBaseCode;
}
