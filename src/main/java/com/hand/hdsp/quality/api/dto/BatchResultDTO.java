package com.hand.hdsp.quality.api.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hand.hdsp.quality.infra.vo.ResultWaringVO;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p>批数据方案结果表 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("批数据方案结果表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@VersionAudit
@ModifyAudit
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

    // =========================非库字段=========================

    @ApiModelProperty(value = "评估方案分组ID")
    private Long groupId;

    @ApiModelProperty(value = "方案名称")
    private String planName;

    @ApiModelProperty(value = "方案任务名称")
    private String planJobName;

    private Long ruleCount;

    private Long exceptionRuleCount;

    private String warningCode;


    @Transient
    private List<BatchResultMarkDTO> batchResultMarkDTOList;

    /**
     * 方案总的告警情况
     */
    private List<ResultWaringVO> resultWaringVOList;


    @ApiModelProperty(value = "校验项总数")
    private Long checkItemCount;

    @ApiModelProperty(value = "异常校验项数")
    private Long exceptionCheckItemCount;

    @Transient
    @ApiModelProperty(value = "数据标准总数")
    @Builder.Default
    private Long dataStandardCount = 0L;

    @Transient
    @ApiModelProperty(value = "异常数据标准数")
    @Builder.Default
    private Long exceptionDataStandardCount = 0L;

    @Transient
    private List<BatchResultBaseDTO> batchResultBaseDTOList;

    @Transient
    private List<Map<String, Object>> exceptionMapList;

    private Long projectId;

    @Transient
    private Long workOrderId;

    @Transient
    private String launchStatus;

    @Transient
    private String workOrderStatus;

}
