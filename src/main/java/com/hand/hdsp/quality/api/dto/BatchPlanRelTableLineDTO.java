package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * <p>批数据方案-表间规则关联关系表 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("批数据方案-表间规则关联关系表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchPlanRelTableLineDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long lineId;

    @ApiModelProperty(value = "XQUA_BATCH_PLAN_REL_TABLE.PLAN_REL_TABLE_ID")
    @NotNull
    private Long planRelTableId;

    @ApiModelProperty(value = "源表字段名")
    @NotBlank
    @Size(max = 50)
    private String sourceFieldName;

    @ApiModelProperty(value = "关联关系 HDSP.XQUA.REL_CODE")
    @NotBlank
    @Size(max = 50)
    private String relCode;

    @ApiModelProperty(value = "关联表字段名")
    @NotBlank
    @Size(max = 50)
    private String relFieldName;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

}
