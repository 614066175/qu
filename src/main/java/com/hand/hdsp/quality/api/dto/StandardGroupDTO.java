package com.hand.hdsp.quality.api.dto;

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

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/23 20:53
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("标准分组表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@VersionAudit
@ModifyAudit
public class StandardGroupDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long groupId;

    @ApiModelProperty(value = "父级分组ID")
    @NotNull
    private Long parentGroupId;

    @ApiModelProperty(value = "分组编码")
    @NotBlank
    @Size(max = 50)
    private String groupCode;

    @ApiModelProperty(value = "分组名称")
    @NotBlank
    @Size(max = 255)
    private String groupName;

    @ApiModelProperty(value = "分组描述")
    private String groupDesc;

    @ApiModelProperty(value = "分组类型BATCH/STREAMING")
    private String groupType;

    @ApiModelProperty(value = "是否启用 1-启用 0-不启用")
    private Integer enabledFlag;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;
}
