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
 * <p>字段标准版本表 数据传输对象</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("字段标准版本表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataFieldVersionDTO extends AuditDomain {

    @ApiModelProperty("版本ID，主键，供其他表做外键")
    private Long versionId;

    @ApiModelProperty(value = "字段标准ID")
    @NotNull
    private Long fieldId;

    @ApiModelProperty(value = "分组ID")
    @NotNull
    private Long groupId;

    @ApiModelProperty(value = "字段名称")
    @NotBlank
    @Size(max = 255)
    private String fieldName;

    @ApiModelProperty(value = "字段注释")
    private String fieldComment;

    @ApiModelProperty(value = "系统常用名")
    private String sysCommonName;

    @ApiModelProperty(value = "标准描述")
    private String standardDesc;

    @ApiModelProperty(value = "字段类型")
    @NotBlank
    @Size(max = 50)
    private String fieldType;

    @ApiModelProperty(value = "字段长度")
    private String fieldLength;

    @ApiModelProperty(value = "数据格式")
    private String dataPattern;

    @ApiModelProperty(value = "值域类型")
    private String valueType;

    @ApiModelProperty(value = "值域")
    private String valueRange;

    @ApiModelProperty(value = "责任部门ID")
    @NotNull
    private Long chargeDeptId;

    @ApiModelProperty(value = "责任人ID")
    @NotNull
    private Long chargeId;

    @ApiModelProperty(value = "责任人电话")
    private String chargeTel;

    @ApiModelProperty(value = "责任人邮箱")
    private String chargeEmail;

    @ApiModelProperty(value = "版本号")
    private Long versionNumber;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

}
