package com.hand.hdsp.quality.api.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * <p>命名标准落标头表 数据传输对象</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("命名标准落标头表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NameStandardContentHeadDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long contentHeadId;

    @ApiModelProperty(value = "命名标准ID")
    @NotNull
    private Long nameStandardId;

    @ApiModelProperty(value = "数据源编码")
    @NotBlank
    @Size(max = 255)
    private String datasourceCode;

    @ApiModelProperty(value = "数据源类型")
    @NotBlank
    @Size(max = 50)
    private String datasourceType;

    @ApiModelProperty(value = "排除规则")
    private String excludeRole;

    @ApiModelProperty(value = "排除说明")
    private String excludeDesc;

    @ApiModelProperty(value = "是否启用 1-启用 0-不启用")
    @NotNull
    private Integer enabledFlag;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

}
