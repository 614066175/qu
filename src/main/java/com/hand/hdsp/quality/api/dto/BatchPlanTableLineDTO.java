package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>批数据方案-表级规则校验项表 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("批数据方案-表级规则校验项表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchPlanTableLineDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long planTableLineId;

    @ApiModelProperty(value = "方案-表级规则表XQUA_BATCH_PLAN_TABLE.PLAN_TABLE_ID")
    @NotNull
    private Long planTableId;

    @ApiModelProperty(value = "校验项 HDSP.XQUA.CHECK_ITEM")
    private String checkItem;

    @ApiModelProperty(value = "比较方式 HDSP.XQUA.COMPARE_WAY")
    private String compareWay;

    @ApiModelProperty(value = "期望值")
    private String expectedValue;

    @ApiModelProperty(value = "自定义SQL")
    private String customSql;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "方案告警等级List")
    private List<PlanWarningLevelDTO> planWarningLevelDTOList;

}
