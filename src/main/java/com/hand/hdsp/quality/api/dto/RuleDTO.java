package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hzero.boot.platform.lov.annotation.LovValue;

import javax.persistence.Id;
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
public class RuleDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    private Long ruleId;

    @ApiModelProperty("规则分组表XQUA_RULE_GROUP.GROUP_ID")
    private Long groupId;

    @ApiModelProperty("规则编码")
    @NotBlank
    @Size(max = 50)
    private String ruleCode;

    @ApiModelProperty("规则名称")
    @NotBlank
    private String ruleName;

    @ApiModelProperty("规则描述")
    private String ruleDesc;

    @ApiModelProperty("校验类别")
    @LovValue(lovCode = "HDSP.XQUA.CHECK_TYPE", meaningField = "checkTypeMeaning")
    private String checkType;

    @ApiModelProperty("规则类型")
    @LovValue(lovCode = "HDSP.XQUA.RULE_TYPE", meaningField = "ruleTypeMeaning")
    private String ruleType;

    @ApiModelProperty("是否异常阻断")
    @NotNull
    private Integer exceptionBlock;

    @ApiModelProperty("权重")
    private Long weight;

    @ApiModelProperty("是否启用 1-启用 0-不启用")
    @NotNull
    private Integer enabledFlag;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "规则校验项List")
    private List<RuleLineDTO> ruleLineDTOList;

    @ApiModelProperty(value = "是否选用标记")
    private Integer selectedFlag = 0;
}
