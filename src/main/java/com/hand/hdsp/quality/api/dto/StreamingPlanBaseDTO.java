package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>实时数据方案-基础配置表 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("实时数据方案-基础配置表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StreamingPlanBaseDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long planBaseId;

    @ApiModelProperty(value = "实时数据评估方案表XQUA_STREAMING_PLAN.PLAN_ID")
    @NotNull
    private Long planId;

    @ApiModelProperty(value = "connector信息")
    private String connectorInfo;

    @ApiModelProperty(value = "topic信息")
    private String topicInfo;

    @ApiModelProperty(value = "方案描述")
    private String planDesc;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "规则List")
    private List<StreamingPlanRuleDTO> streamingPlanRuleDTOList;

    @ApiModelProperty(value = "规则删除的对象List")
    private List<StreamingPlanRuleDTO> deleteStreamingPlanRuleDTOList;

    private Long delayNum;

    private Long cutoffNum;
}
