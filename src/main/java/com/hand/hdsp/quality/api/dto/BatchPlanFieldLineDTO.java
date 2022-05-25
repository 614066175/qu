package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
@VersionAudit
@ModifyAudit
public class BatchPlanFieldLineDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long planLineId;

    @ApiModelProperty(value = "方案-字段规则表XQUA_BATCH_PLAN_FIELD.PLAN_RULE_ID")
    @NotNull
    private Long planRuleId;

    @ApiModelProperty(value = "校验方式 HDSP.XQUA.CHECK_WAY")
    private String checkWay;

    @ApiModelProperty(value = "校验项 HDSP.XQUA.CHECK_ITEM")
    private String checkItem;

    @ApiModelProperty(value = "校验类型 HDSP.XQUA.COUNT_TYPE")
    private String countType;

    @ApiModelProperty(value = "规则字段，多个列逗号拼接")
    @NotBlank
    @Size(max = 1000)
    private String fieldName;

    @ApiModelProperty(value = "维度字段，多个列逗号拼接")
    private String dimensionField;

    @ApiModelProperty(value = "校验字段，多个列逗号拼接")
    @Size(max = 1000)
    private String checkFieldName;

    @ApiModelProperty(value = "正则表达式")
    private String regularExpression;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "字段规则条件List")
    private List<BatchPlanFieldConDTO> batchPlanFieldConDTOList;

    private Long projectId;
}
