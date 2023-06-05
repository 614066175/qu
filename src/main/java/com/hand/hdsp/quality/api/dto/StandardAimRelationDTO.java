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
 * <p>标准落标关系表 数据传输对象</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-30 10:23:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("标准落标关系表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardAimRelationDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long relationId;

    @ApiModelProperty(value = "落标表ID")
    @NotNull
    private Long aimId;

    @ApiModelProperty(value = "落标类型(XSTA.AIM_TYPE AIM：落标，REFERENCE:引用)")
    @NotBlank
    @Size(max = 30)
    private String aimType;

    @ApiModelProperty(value = "评估方案ID")
    private Long planId;

    @ApiModelProperty(value = "方案配置表ID")
    private Long planBaseId;

    @ApiModelProperty(value = "字段规则表ID")
    private Long planRuleId;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    private Long projectId;

}
