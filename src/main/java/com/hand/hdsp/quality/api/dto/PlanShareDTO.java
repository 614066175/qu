package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p> 数据传输对象</p>
 *
 * @author guoliang.li01@hand-china.com 2022-09-26 14:04:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlanShareDTO extends AuditDomain {

    @ApiModelProperty("共享id")
    private Long shareId;

    @ApiModelProperty(value = "分享对象类型")
    @NotBlank
    private String shareObjectType;

    @ApiModelProperty(value = "分享对象ID")
    @NotNull
    private Long shareObjectId;

    @ApiModelProperty(value = "分享来源的项目ID")
    @NotNull
    private Long shareFromProjectId;

    @ApiModelProperty(value = "分享给的项目ID")
    @NotNull
    private Long shareToProjectId;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "项目id(默认为0)")
    @NotNull
    private Long projectId;

    @Transient
    private String projectName;

}
