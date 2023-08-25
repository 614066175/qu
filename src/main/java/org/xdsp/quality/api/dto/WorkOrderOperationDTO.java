package org.xdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * <p> 数据传输对象</p>
 *
 * @author guoliang.li01@hand-china.com 2022-04-20 16:28:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkOrderOperationDTO extends AuditDomain {

    @ApiModelProperty("工单操作id,主键")
    private Long orderOperateId;

    @ApiModelProperty(value = "工单id 关联xqua_work_order的work_order_id")
    @NotBlank
    @Size(max = 30)
    private Long workOrderId;

    @ApiModelProperty(value = "工单状态")
    @NotBlank
    private String workOrderStatus;

    @ApiModelProperty(value = "操作人，关联iam_user 的id")
    @NotNull
    private Long operatorId;

    @ApiModelProperty(value = "工单操作类别")
    @NotNull
    private String operateType;

    @ApiModelProperty(value = "处理意见")
    private String processComment;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "项目id(默认为0)")
    @NotNull
    private Long projectId;


    //操作者名称
    @Transient
    private String operatorName;

}
