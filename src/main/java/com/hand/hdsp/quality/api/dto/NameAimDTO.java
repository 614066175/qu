package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hzero.boot.platform.lov.annotation.LovValue;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * <p>命名落标表 数据传输对象</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("命名落标表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NameAimDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long aimId;

    @ApiModelProperty(value = "命名标准ID 关联表 XSTA_NAME_STANDARD")
    @NotNull
    private Long standardId;

    @ApiModelProperty(value = "数据源编码")
    @NotBlank
    @Size(max = 50)
    private String datasourceCode;

    @LovValue(lovCode = "XCOR.DATASOURCE_TYPE",meaningField = "datasourceTypeMeaning")
    @ApiModelProperty(value = "数据源类型，快码：XCOR.DATASOURCE_TYPE")
    @NotBlank
    @Size(max = 30)
    private String datasourceType;

    @ApiModelProperty(value = "排除规则")
    private String excludeRule;

    @ApiModelProperty(value = "排除说明")
    private String excludeDesc;

    @ApiModelProperty(value = "是否启用 1-启用 0-不启用")
    private Integer enabledFlag;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    //===============================================================================
    //  说明：业务字段
    //===============================================================================

    @Transient
    private String datasourceTypeMeaning;

    @Transient
    private List<NameAimIncludeDTO> nameAimIncludeDTOList;

    @Transient
    private List<NameAimExcludeDTO> nameAimExcludeDTOList;

    private Long projectId;

}
