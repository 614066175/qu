package org.xdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * <p>命名标准排除规则表 数据传输对象</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("命名标准排除规则表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NameAimRuleDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long ruleId;

    @ApiModelProperty(value = "落标ID，关联表 XSTA_NAME_AIM")
    @NotNull
    private Long aimId;

    @ApiModelProperty(value = "排除规则")
    @NotBlank
    @Size(max = 240)
    private String excludeRule;

    @ApiModelProperty(value = "是否启用 1-启用 0-不启用")
    @NotNull
    private Integer enabledFlag;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    private Long projectId;
}
