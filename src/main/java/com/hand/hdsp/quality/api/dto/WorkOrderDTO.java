package com.hand.hdsp.quality.api.dto;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.choerodon.mybatis.domain.AuditDomain;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonInclude;

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
public class WorkOrderDTO extends AuditDomain {

    @ApiModelProperty("工单id,主键")
    private Long workOrderId;

    @ApiModelProperty(value = "工单编码")
    @NotBlank
    @Size(max = 30)
    private String workOrderCode;

    @ApiModelProperty(value = "评估方案id,关联xqua_batch_plan的plan_id")
    @NotNull
    private Long planId;

    @ApiModelProperty(value = "评估方案执行结果id,关联xqua_batch_result_id")
    @NotNull
    private Long resultId;

    @ApiModelProperty(value = "处理人id(关联用户id)")
    @NotNull
    private Long processorsId;

    @ApiModelProperty(value = "紧急程度(值集 HDSP.XQUA.IMMEDIATE_LEVEL)")
    @NotBlank
    @Size(max = 30)
    private String immediateLevel;

    @ApiModelProperty(value = "工单备注")
    private String orderDesc;

    @ApiModelProperty(value = "工单状态(值集 HDSP.XQUA.ORDER_STATUS)")
    @NotBlank
    @Size(max = 30)
    private String workOrderStatus;

    @ApiModelProperty(value = "关联任务类型（DATAX，SQOOP，SQL...）")
    private String relateJobType;

    @ApiModelProperty(value = "关联任务id(数据开发任务，关联hdsp_dispatch.xdis_dispatch_job)")
    private Long relateJobId;

    @ApiModelProperty(value = "工单解决方案")
    private String orderSolution;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "项目id(默认为0)")
    private Long projectId;


    @Transient
    private String planName;

    @Transient
    private String planDesc;

    @Transient
    private BigDecimal planMark;

    @Transient
    private Date planExecTime;

    @Transient
    private String processComment;

    @Transient
    private Long assignId;

    @Transient
    private String assignName;

}
