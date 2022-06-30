package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * <p>表设计表 数据传输对象</p>
 *
 * @author lei.song@hand-china.com 2020-12-28 10:46:58
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("表设计表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomTableDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long customTableId;

    @ApiModelProperty(value = "数据源类型")
    @NotBlank
    @Size(max = 64)
    private String customDatasourceType;

    @ApiModelProperty(value = "数据源编码")
    @NotBlank
    @Size(max = 64)
    private String customDatasourceCode;

    @ApiModelProperty(value = "模式")
    @NotBlank
    @Size(max = 64)
    private String customSchemaName;

    @ApiModelProperty(value = "表类型1，维度表，2事实表，3汇总表，4业务表，5其他表")
    @NotNull
    private String customTableType;

    @ApiModelProperty(value = "表名")
    @NotBlank
    @Size(max = 64)
    private String customTableName;

    @ApiModelProperty(value = "描述")
    private String customDescription;

    @ApiModelProperty(value = "状态，1新建，2已发布，3，已下线")
    @NotNull
    private String customStatus;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "项目管理ID")
    @NotNull
    private Long projectId;


    private Long assetCatalogId;

    private Long chargeId;

    private Long domainId;

    private String warehouseLevel;

    private Long securityId;

    private String releaseException;

    private String updateCycle;

    private String tableSource;


    @ApiModelProperty(value = "是否同时删除物理表")
    @Builder.Default
    private Integer isDeletePhysicalTable = 0;

    @ApiModelProperty(value = "路径")
    private String path;

    @Transient
    private String createdByName;

    @Transient
    private String lastUpdatedByName;


    @Transient
    private String assetCatalogName;

    @Transient
    private String chargeName;

    @Transient
    private String domainName;

    @Transient
    private String securityLevelStr;
}
