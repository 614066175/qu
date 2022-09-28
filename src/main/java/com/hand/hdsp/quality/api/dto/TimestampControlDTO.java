package com.hand.hdsp.quality.api.dto;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * description
 *
 * @author LZL 2018/12/12 14:01
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("时间戳控制表")
@VersionAudit
@ModifyAudit
@FieldNameConstants(prefix = "FIELD_")
public class TimestampControlDTO extends AuditDomain {

    private Long controlId;

    @NotBlank
    private String timestampType;
    @NotNull
    private Date timestampDate;
    @NotNull
    private Long tenantId;

    private String runStatus;

    private String sqoopDate;

    private String lastDataDate;

    private String timestampDesc;

    @ApiModelProperty(value = "当前时间戳日期", required = true)
    private String currentDateTime;
    @ApiModelProperty(value = "同步类型，DATAX/SQOOP/等", required = true)
    private String syncType;
    @ApiModelProperty(value = "增量同步策略，ID/DATE,主键策略/日期策略", required = true)
    private String incrementStrategy;
    @ApiModelProperty(value = "数据源ID,1.4.1之后不用")
    private Long datasourceId;
    @ApiModelProperty(value = "数据源编码")
    private String datasourceCode;
    @ApiModelProperty(value = "数据库schema")
    private String sourceSchema;
    @ApiModelProperty(value = "同步来源表名")
    private String sourceTableName;
    @ApiModelProperty(value = "增量字段名")
    private String incrementColumn;
    @ApiModelProperty(value = "增量字段名")
    private String whereCondition;
    @ApiModelProperty(value = "上次增量主键ID最大值")
    private String lastMaxId;
    @ApiModelProperty(value = "当前增量主键ID最大值")
    private String currentMaxId;


    private String enabledFlag;

    @Transient
    private Boolean success;

    @Transient
    private String lastDateTime;

    @ApiModelProperty(value = "项目ID")
    private Long projectId;
}
