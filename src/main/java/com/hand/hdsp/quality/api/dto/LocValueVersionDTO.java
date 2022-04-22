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
import java.util.Date;

/**
 * <p>loc独立值集表 数据传输对象</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-19 16:38:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("loc独立值集表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocValueVersionDTO extends AuditDomain {

    @ApiModelProperty("主键")
    private Long valueVersionId;

    @ApiModelProperty(value = "代码集版本id，xqua_loc_version表主键")
    @NotNull
    private Long locVersionId;

    @ApiModelProperty(value = "")
    @NotNull
    private Long locValueId;

    @ApiModelProperty(value = "loc表Id")
    @NotNull
    private Long locId;

    @ApiModelProperty(value = "值集代码")
    @NotBlank
    @Size(max = 60)
    private String locCode;

    @ApiModelProperty(value = "")
    private String value;

    @ApiModelProperty(value = "")
    private String meaning;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "标记")
    private String tag;

    @ApiModelProperty(value = "排序号")
    @NotNull
    private Long orderSeq;

    @ApiModelProperty(value = "")
    private String parentValue;

    @ApiModelProperty(value = "有效期起")
    private Date startDateActive;

    @ApiModelProperty(value = "有效期止")
    private Date endDateActive;

    @ApiModelProperty(value = "生效标识：1:生效，0:失效")
    @NotNull
    private Integer enabledFlag;

    @ApiModelProperty(value = "模糊查询条件")
    @Transient
    private String queryString;

}
