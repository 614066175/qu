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
 * <p>loc表 数据传输对象</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("loc表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocDTO extends AuditDomain {

    @ApiModelProperty("")
    private Long locId;

    @ApiModelProperty(value = "loc代码")
    @NotBlank
    @Size(max = 60)
    private String locCode;



    @ApiModelProperty(value = "值集名称")
    @NotBlank
    @Size(max = 240)
    private String locName;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "")
    private String parentLocCode;

    @ApiModelProperty(value = "父级值集租户ID")
    private Long parentTenantId;


    @ApiModelProperty(value = "值字段")
    private String valueField;

    @ApiModelProperty(value = "显示字段")
    private String displayField;

    @ApiModelProperty(value = "是否必须分页")
    @NotNull
    private Integer mustPageFlag;

    @ApiModelProperty(value = "是否启用")
    @NotNull
    private Integer enabledFlag;


    //  非数据库字段

    @Transient
    private Long locVersion;

    @Transient
    private String queryString;

}
