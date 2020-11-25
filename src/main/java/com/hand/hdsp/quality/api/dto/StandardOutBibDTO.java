package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/25 10:43
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("标准落标表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@VersionAudit
@ModifyAudit
public class StandardOutBibDTO extends AuditDomain {

    @ApiModelProperty(value = "表主键ID")
    private Long outbibId;
    @ApiModelProperty(value = "标准ID")
    private Long standardId;
    @ApiModelProperty(value = "标准类型")
    private String standardType;
    @ApiModelProperty(value = "字段名称")
    private String fieldName;
    @ApiModelProperty(value = "字段描述")
    private String fieldDesc;
    @ApiModelProperty(value = "数据源编码")
    private String datasourceCode;
    @ApiModelProperty(value = "数据源类型")
    private String datasourceType;
    @ApiModelProperty(value = "数据库")
    private String datasourceCatalog;
    @ApiModelProperty(value = "模式")
    private String datasourceSchema;
    @ApiModelProperty(value = "表名")
    private String tableName;
    @ApiModelProperty(value = "表描述")
    private String tableDesc;
    @ApiModelProperty(value = "方案ID")
    private Long planId;
    @ApiModelProperty(value = "租户ID")
    private Long tenantId;
}
