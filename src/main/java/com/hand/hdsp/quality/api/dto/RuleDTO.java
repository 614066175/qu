package com.hand.hdsp.quality.api.dto;

import java.util.List;
import javax.persistence.Id;
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
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

/**
 * 规则表 数据传输对象
 *
 * @author wuzhong26857
 * @version 1.0
 * @date 2020/3/23 13:03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("规则表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@VersionAudit
@ModifyAudit
@ExcelSheet(zh = "标准规则", en = "Rule")
public class RuleDTO extends AuditDomain {

    @ExcelColumn(zh = "分组编码",en = "group code")
    private String groupCode;

    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    private Long ruleId;

    @ApiModelProperty("规则分组表XQUA_RULE_GROUP.GROUP_ID")
    private Long groupId;

    @ApiModelProperty("规则编码")
    @NotBlank
    @Size(max = 50)
    @ExcelColumn(zh = "规则编码",en = "rule code")
    private String ruleCode;

    @ApiModelProperty("规则名称")
    @NotBlank
    @ExcelColumn(zh = "规则名称",en="rule name")
    private String ruleName;

    @ApiModelProperty("规则描述")
    @ExcelColumn(zh = "规则描述",en = "rule desc")
    private String ruleDesc;

    @ApiModelProperty("校验类别")
    @LovValue(lovCode = "HDSP.XQUA.CHECK_TYPE", meaningField = "checkTypeMeaning")
    @ExcelColumn(zh = "校验类别",en = "check type")
    private String checkType;

    @ApiModelProperty("规则类型")
    @LovValue(lovCode = "HDSP.XQUA.RULE_TYPE", meaningField = "ruleTypeMeaning")
    @ExcelColumn(zh = "规则类型",en = "rule type")
    private String ruleType;

    @ApiModelProperty("是否异常阻断")
    @NotNull
    @ExcelColumn(zh = "是否异常阻断",en = "rule type" , renderers = BooleanColumnRender.class)
    private Integer exceptionBlock;

    @ApiModelProperty("权重")
    @ExcelColumn(zh = "权重",en = "weight")
    private Long weight;

    @ApiModelProperty("是否启用 1-启用 0-不启用")
    @ExcelColumn(zh = "是否启用",en = "enable flag",renderers = BooleanColumnRender.class)
    @NotNull
    private Integer enabledFlag;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    @ExcelColumn(zh = "租户ID",en = "tenant id")
    private Long tenantId;

    @ApiModelProperty(value = "规则校验项List")
    private List<RuleLineDTO> ruleLineDTOList;

    @Transient
    private String groupDesc;

    @Transient
    private String groupName;

}
