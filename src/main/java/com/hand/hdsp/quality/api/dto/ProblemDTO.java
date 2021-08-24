package com.hand.hdsp.quality.api.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * <p>问题库表 数据传输对象</p>
 *
 * @author lei.song@hand-china.com 2021-08-17 11:47:17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("问题库表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemDTO extends AuditDomain{

    @ApiModelProperty("主键，供外键使用")
    private Long problemId;

    @ApiModelProperty(value = "父级问题ID")
    private Long problemParentId;

    @ApiModelProperty(value = "问题编码")
    @NotBlank
    @Size(max = 255)
    private String problemCode;

    @ApiModelProperty(value = "问题名称")
    @NotBlank
    @Size(max = 255)
    private String problemName;

    @ApiModelProperty(value = "问题类型，值集（）")
    @NotBlank
    @Size(max = 255)
    private String problemType;

    @ApiModelProperty(value = "问题描述")
    private String problemDesc;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    /**
     * 问题父亲名字
     */
    private String problemParentName;
}
