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
import java.util.Date;

/**
 * <p>各种标准审批表 数据传输对象</p>
 *
 * @author fuqiang.luo@hand-china.com 2022-08-31 10:30:34
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("各种标准审批表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardApprovalDTO extends AuditDomain {

    @ApiModelProperty("主键")
    private Long approvalId;

    @ApiModelProperty(value = "标准ID")
    @NotNull
    private Long standardId;

    @ApiModelProperty(value = "标准类型 DATA/NAME/FIELD")
    @NotBlank
    @Size(max = 20)
    private String standardType;

    @ApiModelProperty(value = "申请人ID")
    @NotNull
    private Long applicantId;

    @ApiModelProperty(value = "申请类型（发布/上线/下线）")
    @NotBlank
    @Size(max = 20)
    private String applyType;

    @ApiModelProperty(value = "申请时间")
    private Date applyTime;

    @ApiModelProperty(value = "审批人ID")
    @NotNull
    private Long approverId;

    @ApiModelProperty(value = "审批状态")
    private String approvalStatus;

    @ApiModelProperty(value = "工作流运行实例ID")
    private Long instanceId;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;


    /**
     * 流程名称
     */
    @Transient
    private String flowName;

    /**
     * 申请单号
     */
    @Transient
    private String businessKey;

    /**
     * 当前节点
     */
    @Transient
    private String currentNode;

    /**
     * 申请时间
     */
    @Transient
    private Date applyDate;

    /**
     * 申请部门
     */
    @Transient
    private String applyUnitName;

    /**
     * 申请员工
     */
    @Transient
    private String employeeName;

    /**
     * 申请人电话
     */
    @Transient
    private String employeeTel;

    /**
     * 申请人邮箱
     */
    @Transient
    private String employeeEmail;

    /**
     * 版本
     */
    @Transient
    private String submitVersion;

}
