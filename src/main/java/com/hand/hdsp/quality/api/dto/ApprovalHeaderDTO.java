package com.hand.hdsp.quality.api.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p>申请头表 数据传输对象</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-25 20:35:06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("申请头表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApprovalHeaderDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long approvalId;

    @ApiModelProperty(value = "资源名")
    @NotBlank
    @Size(max = 255)
    private String resourceName;

    @ApiModelProperty(value = "资源描述")
    private String resourceDesc;

    @ApiModelProperty(value = "事项类型")
    @NotBlank
    @Size(max = 50)
    private String itemType;

    @ApiModelProperty(value = "操作名称")
    @NotBlank
    @Size(max = 50)
    private String operation;

    @ApiModelProperty(value = "申请人ID")
    @NotNull
    private Long applyId;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

}
