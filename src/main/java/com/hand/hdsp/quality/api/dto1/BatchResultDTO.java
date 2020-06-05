package com.hand.hdsp.quality.api.dto1;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>批数据方案结果表 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:28:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("批数据方案结果表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchResultDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long resultId;

    @ApiModelProperty(value = "批数据评估方案表XQUA_BATCH_PLAN.PLAN_ID")
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

    @ApiModelProperty(value = "异常信息")
    private String exceptionInfo;

    @ApiModelProperty(value = "最新一次结果标记 1-最新的 0-旧的")
    @NotNull
    private Integer lastFlag;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

}
