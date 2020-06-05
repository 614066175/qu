package com.hand.hdsp.quality.api.dto1;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>批数据方案结果表-表信息 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:28:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("批数据方案结果表-表信息")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchResultBaseDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long resultBaseId;

    @ApiModelProperty(value = "批数据方案结果表XQUA_BATCH_RESULT.RESULT_ID")
    @NotNull
    private Long resultId;

    @ApiModelProperty(value = "批数据方案-基础配置表XQUA_BATCH_PLAN_BASE.PLAN_BASE_ID")
    @NotNull
    private Long planBaseId;

    @ApiModelProperty(value = "校验表名称")
    @NotBlank
    private String tableName;

    @ApiModelProperty(value = "增量校验策略")
    private String incrementStrategy;

    @ApiModelProperty(value = "增量字段")
    private String incrementColumn;

    @ApiModelProperty(value = "条件where")
    private String whereCondition;

    @ApiModelProperty(value = "规则总数")
    private Long ruleCount;

    @ApiModelProperty(value = "异常规则数")
    private Long exceptionRuleCount;

    @ApiModelProperty(value = "校验项总数")
    private Long checkItemCount;

    @ApiModelProperty(value = "异常校验项数")
    private Long exceptionCheckItemCount;

    @ApiModelProperty(value = "数据量（表行数）")
    private Long dataCount;

    @ApiModelProperty(value = "表大小")
    private Long tableSize;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

}