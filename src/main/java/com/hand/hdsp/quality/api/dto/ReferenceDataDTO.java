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
import org.springframework.format.annotation.DateTimeFormat;

import org.hzero.core.base.BaseConstants;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

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
@ApiModel("参考数据头表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@ExcelSheet(zh = "参考数据", en = "Reference Data", rowOffset = 2)
public class ReferenceDataDTO extends AuditDomain {

    @ApiModelProperty("表主键ID")
    private Long dataId;

    @ApiModelProperty(value = "参考数据编码")
    @NotBlank
    @Size(max = 128)
    @ExcelColumn(zh = "参考数据编码", en = "Data code", order = 2)
    private String dataCode;

    @ApiModelProperty(value = "参考数据名称")
    @NotBlank
    @Size(max = 128)
    @ExcelColumn(zh = "参考数据名称", en = "Data name", order = 3)
    private String dataName;

    @ApiModelProperty(value = "参考数据描述")
    @ExcelColumn(zh = "参考数据描述", en = "Data description", order = 4)
    private String dataDesc;

    @ApiModelProperty(value = "父参考数据")
    private Long parentDataId;

    @ApiModelProperty(value = "参考数据分组-通用分组的id")
    @NotNull
    private Long dataGroupId;

    @ApiModelProperty(value = "参考数据状态")
    @NotBlank
    @Size(max = 32)
    private String dataStatus;

    @ApiModelProperty(value = "发布人")
    private Long releaseBy;

    @ApiModelProperty(value = "发布日期")
    private Date releaseDate;

    @ApiModelProperty(value = "责任人部门id")
    private Long responsibleDeptId;

    @ApiModelProperty(value = "责任人id")
    @NotNull
    private Long responsiblePersonId;

    @ApiModelProperty(value = "责任人电话")
    @ExcelColumn(zh = "责任人电话", en = "Responsible Person Tel", order = 8)
    private String responsiblePersonTel;

    @ApiModelProperty(value = "责任人邮箱")
    @ExcelColumn(zh = "责任人邮箱", en = "Responsible Person Email", order = 9)
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
    @ExcelColumn(zh = "责任部门", en = "Responsible department", order = 6)
    private String responsibleDeptName;

    @ApiModelProperty(value = "责任人名")
    @Transient
    @ExcelColumn(zh = "责任人", en = "Responsible Person", order = 7)
    private String responsiblePersonName;

    @ApiModelProperty(value = "责任人编码")
    @Transient
    private String responsiblePersonCode;

    @ApiModelProperty(value = "工作流实例id")
    @Transient
    private Long instanceId;

    @ApiModelProperty(value = "父参考数据编码-导入导出用")
    @Transient
    @ExcelColumn(zh = "父参考数据编码", en = "Parent data code", order = 5)
    private String parentDataCode;

    @ApiModelProperty(value = "分组全路径-导入导出用")
    @Transient
    @ExcelColumn(zh = "分组全路径", en = "Group path", order = 1)
    private String groupPath;

    @ApiModelProperty(value = "父参考数据名称-导入导出用")
    @Transient
    private String parentDataName;

    @ApiModelProperty(value = "用于勾选要导出的数据")
    @Transient
    private List<Long> exportIds;

    @ExcelColumn(zh = "参考值列表", en = "referenceDataValueDTOList", child = true)
    @Transient
    private List<ReferenceDataValueDTO> referenceDataValueDTOList;

    @Transient
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATETIME)
    private Date releaseDateFrom;

    @Transient
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATETIME)
    private Date releaseDateTo;

    @Transient
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATETIME)
    private Date creationDateFrom;

    @Transient
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATETIME)
    private Date creationDateTo;

    @Transient
    private List<Long> dataGroupIds;

}
