package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 规则分组表 数据传输对象
 *
 * @author wuzhong26857
 * @version 1.0
 * @date 2020/3/23 12:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("规则分组表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RuleGroupDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    private Long groupId;

    @ApiModelProperty("父级分组ID")
    private Long parentGroupId;

    @ApiModelProperty(value = "分组编码")
    @NotBlank
    @Size(max = 50)
    private String groupCode;

    @ApiModelProperty(value = "分组名称")
    @NotBlank
    private String groupName;

    @ApiModelProperty(value = "分组描述")
    private String groupDesc;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;
}
