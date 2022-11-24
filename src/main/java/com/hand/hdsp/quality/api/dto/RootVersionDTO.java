package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.choerodon.mybatis.domain.AuditDomain;

/**
 * description
 *
 * @author XIN.SHENG01@HAND-CHINA.COM 2022/11/21 14:42
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RootVersionDTO extends AuditDomain {
    @Id
    private Long id;
    @ApiModelProperty(value = "租户ID",required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "项目ID",required = true)
    @NotNull
    private Long projectId;
    @ApiModelProperty(value = "词根id",required = true)
    @NotNull
    private Long rootId;
    @ApiModelProperty(value = "词根英文全称")
    private String rootEn;
    @ApiModelProperty(value = "词根英文简称",required = true)
    @NotBlank
    private String rootEnShort;
    @ApiModelProperty(value = "词根中文名，以,做分隔符",required = true)
    @NotBlank
    private String rootName;
    @ApiModelProperty(value = "词根描述")
    private String rootDesc;
    @ApiModelProperty(value = "责任部门ID")
    private Long chargeDeptId;
    @ApiModelProperty(value = "责任人ID",required = true)
    @NotNull
    private Long chargeId;
    @ApiModelProperty(value = "分组ID")
    private Long groupId;
    @ApiModelProperty(value = "版本号",required = true)
    @NotNull
    private Long versionNumber;
}
