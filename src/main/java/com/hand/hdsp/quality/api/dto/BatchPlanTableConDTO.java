package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>批数据方案-表级规则条件表 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:03:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("批数据方案-表级规则条件表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchPlanTableConDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long conditionId;

    @ApiModelProperty(value = "表级校验项ID XQUA_BATCH_PLAN_TABLE_LINE.PLAN_LINE_ID")
    @NotNull
    private Long planLineId;

    @ApiModelProperty(value = "条件where")
    private String whereCondition;

    @ApiModelProperty(value = "比较方式 HDSP.XQUA.COMPARE_WAY")
    private String compareWay;

    @ApiModelProperty(value = "告警等级json")
    private String warningLevel;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "方案告警等级List")
    private List<WarningLevelDTO> warningLevelDTOList;
}
