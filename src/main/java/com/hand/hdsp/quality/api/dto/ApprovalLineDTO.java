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
 * <p>申请行表 数据传输对象</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-25 20:35:06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("申请行表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApprovalLineDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long approvalLineId;

    @ApiModelProperty(value = "申请头表ID")
    @NotNull
    private Long approvalId;

    @ApiModelProperty(value = "申请行表")
    @NotBlank
    @Size(max = 50)
    private String operation;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

}
