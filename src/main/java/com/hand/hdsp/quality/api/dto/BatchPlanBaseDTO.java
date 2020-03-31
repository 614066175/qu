package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>批数据方案-基础配置表 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("批数据方案-基础配置表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchPlanBaseDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long planBaseId;

    @ApiModelProperty(value = "批数据评估方案表XQUA_BATCH_PLAN.PLAN_ID")
    @NotNull
    private Long planId;

    @ApiModelProperty(value = "数据源类型(快码：HDSP.DATASOURCE_TYPE)")
    private String datasourceType;

    @ApiModelProperty(value = "数据源ID")
    private Long datasourceId;

    @ApiModelProperty(value = "数据库")
    private String datasourceSchema;

    @ApiModelProperty(value = "表名")
    private String tableName;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "表级规则List")
    private List<BatchPlanTableDTO> batchPlanTableDTOList;

    @ApiModelProperty(value = "字段规则List")
    private List<BatchPlanFieldDTO> batchPlanFieldDTOList;

    @ApiModelProperty(value = "表间规则List")
    private List<BatchPlanRelTableDTO> batchPlanRelTableDTOList;

    private Long tableNum;

    private Long fieldNum;

    private Long relTableNum;

    private String datasourceName;
}
