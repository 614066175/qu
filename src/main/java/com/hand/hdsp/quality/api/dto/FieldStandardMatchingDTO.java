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
 * <p>字段标准匹配表 数据传输对象</p>
 *
 * @author shijie.gao@hand-china.com 2022-11-30 10:31:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("字段标准匹配表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldStandardMatchingDTO extends AuditDomain {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    @ApiModelProperty(value = "字段类型")
    private String fieldType;

    @ApiModelProperty(value = "字段注释")
    private String fieldComment;

    @ApiModelProperty(value = "字段描述")
    private String fieldDescription;

    @ApiModelProperty(value = "匹配状态:( 匹配中,匹配成功,匹配失败)")
    @NotBlank
    @Size(max = 120)
    private String matchingStatus;

    @ApiModelProperty(value = "来源：(词根翻译 , 字段标准)")
    private String source;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "租户id")
    private Long tenantId;

}
