package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * <p>实时数据方案结果表-基础信息 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("实时数据方案结果表-基础信息")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StreamingResultBaseDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long resultBaseId;

    @ApiModelProperty(value = "实时数据方案结果表XQUA_STREAMING_RESULT.RESULT_ID")
    @NotNull
    private Long resultId;

    @ApiModelProperty(value = "connector信息")
    private String connectorInfo;

    @ApiModelProperty(value = "topic信息")
    private String topicInfo;

    @ApiModelProperty(value = "规则总数")
    private Long ruleCount;

    @ApiModelProperty(value = "异常规则数")
    private Long exceptionRuleCount;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    // =========================非库字段=========================

    private Long redWarnCounts;

    private Long orangeWarnCounts;

}
