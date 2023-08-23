package org.xdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * <p>标准落标表 数据传输对象</p>
 *
 * @author guoliangli01.@hand-china.com 2020-11-25 17:03:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("标准落标表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardOutbibDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long outbibId;

    @ApiModelProperty(value = "标准ID")
    @NotNull
    private Long standardId;

    @ApiModelProperty(value = "标准类型（数据标准，字段标准）")
    @NotBlank
    @Size(max = 50)
    private String standardType;

    @ApiModelProperty(value = "字段名称")
    @NotBlank
    @Size(max = 255)
    private String fieldName;

    @ApiModelProperty(value = "字段描述")
    private String fieldDesc;

    @ApiModelProperty(value = "数据源编码")
    @NotBlank
    @Size(max = 255)
    private String datasourceCode;

    @ApiModelProperty(value = "数据源类型")
    @NotBlank
    @Size(max = 50)
    private String datasourceType;

    @ApiModelProperty(value = "数据库")
    @NotBlank
    @Size(max = 255)
    private String datasourceCatalog;

    @ApiModelProperty(value = "数据库模式")
    private String datasourceSchema;

    @ApiModelProperty(value = "表名称")
    @NotBlank
    @Size(max = 255)
    private String tableName;

    @ApiModelProperty(value = "表描述")
    private String tableDesc;

    @ApiModelProperty(value = "评估方案ID")
    private Long planId;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    private Long projectId;

}
