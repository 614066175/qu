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
 * <p>命名标准落标排除表 数据传输对象</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("命名标准落标排除表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NameStandardContentExcludeDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long contentExcludeId;

    @ApiModelProperty(value = "落表行表ID")
    @NotNull
    private Long contentLineId;

    @ApiModelProperty(value = "排除的表编码")
    @NotBlank
    @Size(max = 255)
    private String tableCode;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

}
