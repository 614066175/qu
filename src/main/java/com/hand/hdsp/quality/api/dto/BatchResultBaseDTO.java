package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hand.hdsp.quality.infra.vo.ResultWaringVO;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * <p>批数据方案结果表-表信息 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
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
    @Size(max = 50)
    private String tableName;

    @ApiModelProperty(value = "规则总数")
    @NotNull
    private Long ruleCount;

    @ApiModelProperty(value = "异常规则数")
    private Long exceptionRuleCount;

    @ApiModelProperty(value = "校验项总数")
    private Long checkItemCount;

    @ApiModelProperty(value = "异常校验项数")
    private Long exceptionCheckItemCount;

    @ApiModelProperty(value = "数据量")
    private Long dataCount;

    @ApiModelProperty(value = "表大小")
    private Long tableSize;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    // =========================非库字段=========================

    private List<ResultWaringVO> resultWaringVOS;

}
