package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>批数据方案-字段规则校验项表 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("批数据方案-字段规则校验项表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchPlanFieldLineDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long planFieldLineId;

    @ApiModelProperty(value = "方案-字段规则表XQUA_BATCH_PLAN_FIELD.PLAN_FIELD_ID")
    @NotNull
    private Long planFieldId;

    @ApiModelProperty(value = "校验项 HDSP.XQUA.CHECK_ITEM")
    private String checkItem;

    @ApiModelProperty(value = "校验方式 HDSP.XQUA.CHECK_WAY")
    private String checkWay;

    @ApiModelProperty(value = "比较方式 HDSP.XQUA.COMPARE_WAY")
    private String compareWay;

    @ApiModelProperty(value = "阈值")
    private String expectedValue;

    @ApiModelProperty(value = "正则表达式")
    private String regularExpression;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "方案告警等级List")
    private List<PlanWarningLevelDTO> planWarningLevelDTOList;
}
