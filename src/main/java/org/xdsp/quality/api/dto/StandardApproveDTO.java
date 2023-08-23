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
 * <p>标准申请记录表 数据传输对象</p>
 *
 * @author guoliangli01.@hand-china.com 2020-11-25 17:03:12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("标准申请记录表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardApproveDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long approveId;

    @ApiModelProperty(value = "资源名(标准名)")
    @NotBlank
    @Size(max = 255)
    private String standardName;

    @ApiModelProperty(value = "资源描述(标准描述)")
    private String standardDesc;

    @ApiModelProperty(value = "事项类型（标准类型）")
    @NotBlank
    @Size(max = 255)
    private String standardType;

    @ApiModelProperty(value = "操作类型")
    @NotBlank
    @Size(max = 50)
    private String operation;

    @ApiModelProperty(value = "申请人ID")
    @NotNull
    private Long applyId;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    private Long projectId;

}
