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
import java.util.List;

/**
 * <p>字段标准表 数据传输对象</p>
 *
 * @author wsl 2020-11-25 15:38:21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("字段标准表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataFieldDTO extends AuditDomain {

    @ApiModelProperty("字段标准ID，主键，供其他表做外键")
    private Long fieldId;

    @ApiModelProperty(value = "分组ID")
    @NotNull
    private Long groupId;

    @ApiModelProperty(value = "字段名称")
    @NotBlank
    @Size(max = 255)
    private String fieldName;

    @ApiModelProperty(value = "字段注释")
    @NotBlank
    @Size(max = 255)
    private String fieldComment;

    @ApiModelProperty(value = "系统常用名")
    private String sysCommonName;

    @ApiModelProperty(value = "标准描述")
    private String standardDesc;

    @ApiModelProperty(value = "字段类型 (HDSP.XDMP.LABEL_DATA_TYPE)")
    @NotBlank
    private String fieldType;

    @ApiModelProperty(value = "字段长度")
    private String fieldLength;

    @ApiModelProperty(value = "数据格式")
    private String dataPattern;

    @ApiModelProperty(value = "值域类型（快码HDSP.XSTA.VALUE_TYPE）")
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

    @ApiModelProperty(value = "字段标准状态(快码HSDP.XSTA.STANDARD_STATUS)")
    @NotBlank
    @Size(max = 50)
    private String status;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @Transient
    private List<StandardExtraDTO> standardExtraDTOList;
}
