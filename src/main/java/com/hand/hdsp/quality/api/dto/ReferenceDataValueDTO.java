package com.hand.hdsp.quality.api.dto;

import javax.persistence.Transient;
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
 * <p>参考数据值 数据传输对象</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("参考数据值")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReferenceDataValueDTO extends AuditDomain {

    @ApiModelProperty("参考值主键ID")
    private Long valueId;

    @ApiModelProperty(value = "参考数据头表ID")
    @NotNull
    private Long dataId;

    @ApiModelProperty(value = "参考值")
    @NotBlank
    @Size(max = 128)
    private String value;

    @ApiModelProperty(value = "含义")
    @NotBlank
    @Size(max = 128)
    private String valueMeaning;

    @ApiModelProperty(value = "排序序列")
    @NotNull
    private Long valueSeq;

    @ApiModelProperty(value = "描述")
    private String valueDesc;

    @ApiModelProperty(value = "父参考值")
    private Long parentValueId;

    @ApiModelProperty(value = "启用标志 1-启用 0-禁用")
    @NotNull
    private Integer enabledFlag;

    @ApiModelProperty(value = "项目ID")
    @NotNull
    private Long projectId;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "关键字搜索 参考值 含义的模糊")
    @Transient
    private String searchKey;

}
