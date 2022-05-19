package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.beans.Transient;
import java.math.BigDecimal;

/**
 * <p>标准落标表 数据传输对象</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-05-17 15:20:56
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("标准落标表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardStatisticsDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long statisticsId;

    @ApiModelProperty("外键")
    private Long aimId;

    @ApiModelProperty(value = "表名称")
    @NotBlank
    @Size(max = 120)
    private String tableName;

    @ApiModelProperty(value = "表描述")
    private String tableDesc;

    @ApiModelProperty(value = "数据源类型")
    @NotBlank
    @Size(max = 120)
    private String datasourceType;

    @ApiModelProperty(value = "行数")
    private Long rowNum;

    @ApiModelProperty(value = "非空行数")
    private Long nonNullRow;

    @ApiModelProperty(value = "合规行数")
    private Long compliantRow;

    @ApiModelProperty(value = "合规函数比例")
    private String compliantRate;

    @ApiModelProperty(value = "非空行合规比例")
    private String acompliantRate;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "项目id")
    @NotNull
    private Long projectId;

    @ApiModelProperty(value = "表字段描述")
    @NotBlank
    @Size(max = 120)
    private String fieldDesc;





}
