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
 * <p>命名标准表 数据传输对象</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("命名标准表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NameStandardDTO extends AuditDomain {

    @ApiModelProperty("命名标准ID，主键，供其他表做外键")
    private Long standardId;

    @ApiModelProperty(value = "分组ID，关联表XSTA_STANDARD_GROUP")
    @NotNull
    private Long groupId;

    @ApiModelProperty(value = "命名标准编码")
    @NotBlank
    @Size(max = 50)
    private String standardCode;

    @ApiModelProperty(value = "命名标准名称")
    @NotBlank
    @Size(max = 255)
    private String standardName;

    @ApiModelProperty(value = "命名标准描述")
    private String standardDesc;

    @ApiModelProperty(value = "命名标准类型")
    @NotBlank
    @Size(max = 50)
    private String standardType;

    @ApiModelProperty(value = "命名标准规则")
    @NotBlank
    @Size(max = 255)
    private String standardRole;

    @ApiModelProperty(value = "是否忽略大小写 1-忽略 0-不忽略")
    @NotNull
    private Integer ignoreCaseFlag;

    @ApiModelProperty(value = "责任人ID")
    @NotNull
    private Long chargeId;

    @ApiModelProperty(value = "责任部门ID")
    @NotNull
    private Long chargeDeptId;

    @ApiModelProperty(value = "责任人电话")
    private String chargeTel;

    @ApiModelProperty(value = "责任人邮箱")
    private String chargeEmail;

    @ApiModelProperty(value = "是否启用 1-启用 0-不启用")
    @NotNull
    private Integer enabledFlag;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

}
