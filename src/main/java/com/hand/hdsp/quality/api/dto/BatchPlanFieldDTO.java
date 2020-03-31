package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * <p>批数据方案-字段规则表 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("批数据方案-字段规则表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchPlanFieldDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long planFieldId;

    @ApiModelProperty(value = "方案-基础配置表XQUA_BATCH_PLAN_BASE.PLAN_BASE_ID")
    @NotNull
    private Long planBaseId;

    @ApiModelProperty(value = "列名")
    @NotBlank
    @Size(max = 50)
    private String fieldName;

    @ApiModelProperty(value = "列描述")
    private String fieldDesc;

    @ApiModelProperty(value = "规则编码")
    @NotBlank
    @Size(max = 50)
    private String ruleCode;

    @ApiModelProperty(value = "规则名称")
    @NotBlank
    @Size(max = 255)
    private String ruleName;

    @ApiModelProperty(value = "规则描述")
    private String ruleDesc;

    @ApiModelProperty(value = "校验类别 HDSP.XQUA.CHECK_TYPE")
    private String checkType;

    @ApiModelProperty(value = "规则类型 HDSP.XQUA.RULE_TYPE")
    private String ruleType;

    @ApiModelProperty(value = "是否异常阻断")
    @NotNull
    private Integer exceptionBlock;

    @ApiModelProperty(value = "权重")
    private Long weight;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "字段规则校验项List")
    private List<BatchPlanFieldLineDTO> batchPlanFieldLineDTOList;

    @ApiModelProperty(value = "告警等级List")
    private List<PlanWarningLevelDTO> planWarningLevelDTOList;
}
