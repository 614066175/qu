package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * <p>标准落标统计表 数据传输对象</p>
 *
 * @author guoliang.li01@hand-china.com 2022-06-16 14:38:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("标准落标统计表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AimStatisticsDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long statisticsId;

    @ApiModelProperty(value = "落标主键")
    @NotNull
    private Long aimId;

    @ApiModelProperty(value = "行数")
    private Long rowNum;

    @ApiModelProperty(value = "非空行数")
    private Long nonNullRow;

    @ApiModelProperty(value = "合规行数")
    private Long compliantRow;

    @ApiModelProperty(value = "合规函数比例")
    private BigDecimal compliantRate;

    @ApiModelProperty(value = "非空行合规比例")
    private BigDecimal acompliantRate;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "项目id")
    @NotNull
    private Long projectId;

    /**
     * 验证标识
     */
    @Transient
    private boolean validFlag;

    //字段标准id
    @Transient
    private Long fieldId;


    @Transient
    private String datasourceCode;

    @Transient
    private String schemaName;

    @Transient
    private String tableName;

    @Transient
    private String tableDesc;

    @Transient
    private String fieldName;

    @Transient
    private String fieldDesc;

    @Transient
    private String standardId;

    @Transient
    private String standardType;


    @Transient
    private Long totalRowNum;

    @Transient
    private Long totalNonNullRow;

    @Transient
    private Long totalCompliantRow;

    @Transient
    private BigDecimal totalCompliantRate;

    @Transient
    private BigDecimal totalAcompliantRate;

}
