package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hand.hdsp.quality.domain.entity.PlanGroup;
import com.hand.hdsp.quality.domain.entity.StreamingResult;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * <p>实时数据评估方案表 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("实时数据评估方案表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StreamingPlanDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long planId;

    @ApiModelProperty(value = "评估方案分组表XQUA_PLAN_GROUP.GROUP_ID")
    @NotNull
    private Long groupId;

    @ApiModelProperty(value = "方案编码")
    @NotBlank
    @Size(max = 50)
    private String planCode;

    @ApiModelProperty(value = "方案名称")
    @NotBlank
    @Size(max = 255)
    private String planName;

    @ApiModelProperty(value = "方案描述")
    private String planDesc;

    @ApiModelProperty(value = "告警发送代码")
    private String warningCode;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    // =========================非库字段=========================

    private StreamingResult streamingResult;

    private PlanGroup planGroup;
}
