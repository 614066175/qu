package org.xdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;
import org.xdsp.quality.infra.render.BooleanColumnRender;

import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

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
@ExcelSheet(zh = "标准规则", en = "Rule", rowOffset = 2)
public class RuleDTO extends AuditDomain {

    @ExcelColumn(zh = "分组全路径", en = "rule code", order = 0)
    private String groupPath;

    private String groupCode;

    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    private Long ruleId;

    @ApiModelProperty("规则分组表XQUA_RULE_GROUP.GROUP_ID")
    private Long groupId;

    @ApiModelProperty("规则编码")
    @NotBlank
    @Size(max = 50)
    @ExcelColumn(zh = "规则编码", en = "rule code", order = 1)
    private String ruleCode;

    @ApiModelProperty("规则名称")
    @NotBlank
    @ExcelColumn(zh = "规则名称", en = "rule name", order = 2)
    private String ruleName;

    @ApiModelProperty("规则描述")
    @ExcelColumn(zh = "规则描述", en = "rule desc", order = 6)
    private String ruleDesc;

    @ApiModelProperty("校验类别")
    @LovValue(value = "XQUA.CHECK_TYPE", meaningField = "checkTypeMeaning")
    private String checkType;

    @ExcelColumn(zh = "评估指标", en = "check type", order = 3)
    private String checkTypeMeaning;

    @ApiModelProperty("规则类型")
    private String ruleType;

    @ApiModelProperty("是否异常阻断")
    @NotNull
    @ExcelColumn(zh = "是否异常阻断", en = "exception block", renderers = BooleanColumnRender.class, order = 4)
    private Integer exceptionBlock;

    @Transient
    private String exceptionBlockFlag;

    @ApiModelProperty("权重")
    @ExcelColumn(zh = "权重", en = "weight", order = 5)
    private Long weight;

    @ApiModelProperty("是否启用 1-启用 0-不启用")
    @NotNull
    private Integer enabledFlag;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "规则校验项List")
    private List<RuleLineDTO> ruleLineDTOList;

    private String groupName;

    private String groupDesc;

    @ApiModelProperty("校验方式")
//    @ExcelColumn(zh = "校验方式", en = "check way")
    @LovValue(value = "XQUA.CHECK_WAY", meaningField = "checkWayMeaning")
    private String checkWay;

    @ExcelColumn(zh = "校验方式", en = "check way", order = 7)
    private String checkWayMeaning;

    @ApiModelProperty("校验项")
//    @ExcelColumn(zh = "校验项", en = "check item")
    @LovValue(value = "XQUA.CHECK_ITEM", meaningField = "checkItemMeaning")
    private String checkItem;

    @ExcelColumn(zh = "校验项", en = "check item", order = 8)
    private String checkItemMeaning;

    @ApiModelProperty("校验类型 XQUA.COUNT_TYPE")
//    @ExcelColumn(zh = "校验类型", en = "count type")
    @LovValue(lovCode = "XQUA.COUNT_TYPE", meaningField = "countTypeMeaning")
    private String countType;

    @ExcelColumn(zh = "校验类型", en = "count type", order = 9)
    private String countTypeMeaning;

    @ApiModelProperty("比较方式")
//    @ExcelColumn(zh = "比较方式", en = "compare way")
    @LovValue(lovCode = "XQUA.COMPARE_WAY", meaningField = "compareWayMeaning")
    private String compareWay;

    @ExcelColumn(zh = "比较方式", en = "compare way", order = 10)
    private String compareWayMeaning;

    @ApiModelProperty("正则表达式")
    @ExcelColumn(zh = "正则表达式", en = "regular expression", order = 11)
    private String regularExpression;

    @ApiModelProperty(value = "告警等级json")
    @ExcelColumn(zh = "告警配置", en = "warning level", order = 12)
    private String warningLevel;

    private Long projectId;

    /**
     * 分组集合
     */
    @Transient
    private Long[] groupArrays;

    @Transient
    //平台级标识 Y/N
    @ExcelColumn(zh = "平台级标识", en = "isPlatformFlag", order = 13)
    private String isPlatformFlag;
}
