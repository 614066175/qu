package com.hand.hdsp.quality.api.dto;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * <p>参考数据头表 数据传输对象</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("参考数据历史表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReferenceDataHistoryDTO extends AuditDomain {

    @ApiModelProperty("主键")
    private Long historyId;

    @ApiModelProperty(value = "参考数据头表ID")
    @NotNull
    private Long dataId;

    @ApiModelProperty(value = "参考数据编码")
    @NotBlank
    @Size(max = 128)
    private String dataCode;

    @ApiModelProperty(value = "参考数据名称")
    @NotBlank
    @Size(max = 128)
    private String dataName;

    @ApiModelProperty(value = "参考数据描述")
    private String dataDesc;

    @ApiModelProperty(value = "父参考数据")
    private Long parentDataId;

    @ApiModelProperty(value = "父参考数据编码")
    private String parentDataCode;

    @ApiModelProperty(value = "父参考数据名称")
    private String parentDataName;


    @ApiModelProperty(value = "参考数据分组-通用分组的id")
    private Long dataGroupId;

    @ApiModelProperty(value = "参考数据分组路径")
    private Long groupPath;

    @ApiModelProperty(value = "参考数据值数据json")
    private String dataValueJson;

    @ApiModelProperty(value = "版本号")
    @NotNull
    private Long versionNumber;

    @ApiModelProperty(value = "发布人")
    @NotNull
    private Long releaseBy;

    @ApiModelProperty(value = "发布时间")
    @NotNull
    private Date releaseDate;

    @ApiModelProperty(value = "责任人部门id")
    private Long responsibleDeptId;

    @ApiModelProperty(value = "责任人id")
    @NotNull
    private Long responsiblePersonId;

    @ApiModelProperty(value = "责任人电话")
    private String responsiblePersonTel;

    @ApiModelProperty(value = "责任人邮箱")
    private String responsiblePersonEmail;

    @ApiModelProperty(value = "项目ID")
    @NotNull
    private Long projectId;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "分组名")
    @Transient
    private String dataGroupName;

    @ApiModelProperty(value = "发布人")
    @Transient
    private String publisher;

    @ApiModelProperty(value = "责任部门名")
    @Transient
    private String responsibleDeptName;

    @ApiModelProperty(value = "责任人名")
    @Transient
    private String responsiblePersonName;

    @ApiModelProperty(value = "责任人编码")
    @Transient
    private String responsiblePersonCode;

    @ApiModelProperty(value = "是否为当前版本 1-是 0-否")
    @Transient
    private Integer currentVersion;

    @ApiModelProperty(value = "参考值列表")
    @Transient
    private List<SimpleReferenceDataValueDTO> referenceDataValueDTOList;

}
