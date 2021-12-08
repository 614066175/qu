package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>实时数据方案结果表 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("实时数据方案结果表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@VersionAudit
@ModifyAudit
public class StreamingResultDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long resultId;

    @ApiModelProperty(value = "实时数据评估方案表XQUA_STREAMING_PLAN.PLAN_ID")
    @NotNull
    private Long planId;

    @ApiModelProperty(value = "评估分数")
    private BigDecimal mark;

    @ApiModelProperty(value = "开始时间")
    private Date startDate;

    @ApiModelProperty(value = "结束时间")
    private Date endDate;

    @ApiModelProperty(value = "方案状态 HDSP.XQUA.PLAN_STATUS (未评估、运行中、执行成功、执行失败)")
    private String planStatus;

    @ApiModelProperty(value = "下次评估时间")
    private Date nextCountDate;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    // =========================非库字段=========================

    private Long groupId;

    private String planName;

    private Long ruleCount;

    private Long exceptionRuleCount;

    private Long projectId;

}
