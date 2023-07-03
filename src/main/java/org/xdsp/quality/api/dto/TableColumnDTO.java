package org.xdsp.quality.api.dto;

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
 * <p>表设计字段表 数据传输对象</p>
 *
 * @author lei.song@hand-china.com 2020-12-28 10:58:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("表设计字段表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TableColumnDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long columnId;

    @ApiModelProperty(value = "表设计主键")
    @NotNull
    private Long customTableId;

    @ApiModelProperty(value = "字段名")
    @NotBlank
    @Size(max = 64)
    private String columnName;

    @ApiModelProperty(value = "描述")
    @Builder.Default
    private String description = "";

    @ApiModelProperty(value = "字段类型1，字符串，2时间，3整型，4长整型，5小数")
    @NotNull
    private String columnType;

    @ApiModelProperty(value = "字段长度")
    @Builder.Default
    private Integer columnLength = 0;

    @ApiModelProperty(value = "字段如果为小数的精度")
    @Builder.Default
    private Integer columnDecimal = 0;

    @ApiModelProperty(value = "是否可空，1可空 0 不可空")
    @NotNull
    @Builder.Default
    private Integer nullFlag=1;

    @ApiModelProperty(value = "是否为当前时间 1 是  0 不是")
    @Builder.Default
    private Integer currentDateFlag=0;

    @ApiModelProperty(value = "默认值")
    @Builder.Default
    private String defaultValue = "";

    @ApiModelProperty(value = "是否主键1 是  0 不是")
    @Builder.Default
    private Integer primaryKeyFlag = 0;

    @ApiModelProperty(value = "是否自增1 是  0 不是")
    @Builder.Default
    private Integer increaseFlag = 0;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "项目管理ID")
    @NotNull
    private Long projectId;

    //序号
    private Long sortNumber;

    //引用主键（FIELD:xqua_field_standard.field_id，INDEX:xidx_index.index_id）
    private Long quoteId;

    //引用类型 （FIELD,INDEX）
    private String quoteType;

    //引用的名称
    @Transient
    private String quoteIndexName;

    @Transient
    private String quoteFieldName;

    @Transient
    private Long dataTableId;

    @Transient
    private Long assignId;

    @Transient
    private Long demensionDataTableId;

    @Transient
    private String demensionDataTableName;

    @Transient
    private Long indexId;

    @Transient
    private String indexName;

    @Transient
    private String assignType;

    @Transient
    private String assignStatus;

    @NotBlank
    @Size(max = 64)
    @Transient
    private String datasourceType;

    @NotBlank
    @Size(max = 64)
    @Transient
    private String datasourceCode;

    @NotBlank
    @Size(max = 64)
    @Transient
    private String schemaName;

    @NotBlank
    @Size(max = 64)
    @Transient
    private String tableName;

    @ApiModelProperty(value = "清空指标关联的id")
    @Transient
    private Long deleteIndexId;

    @ApiModelProperty(value = "清空维度关联的id")
    @Transient
    private Long deleteAssignId;

}
