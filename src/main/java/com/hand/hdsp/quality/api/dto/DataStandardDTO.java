package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * <p>数据标准表 数据传输对象</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("数据标准表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@ExcelSheet(zh = "数据标准", en = "Data Standard",rowOffset = 2)
public class DataStandardDTO extends AuditDomain{

    @ApiModelProperty("数据标准ID，主键，供其他表做外键")
    private Long standardId;


    @ApiModelProperty(value = "分组ID")
    @NotNull
    private Long groupId;

    @Transient
    @ExcelColumn(zh = "分组全路径", en = "分组全路径")
    private String groupPath;

    @Transient
//    @ExcelColumn(zh = "分组编码", en = "groupCode")
    private String groupCode;

    @Transient
//    @ExcelColumn(zh = "分组名称", en = "groupName")
    private String groupName;

    @Transient
//    @ExcelColumn(zh = "分组描述", en = "groupDesc")
    private String groupDesc;

    @ApiModelProperty(value = "数据标准编码")
    @NotBlank
    @Size(max = 50)
    @ExcelColumn(zh = "标准编码", en = "standardCode" )
    private String standardCode;

    @Transient
    private Long instanceId;


    @ApiModelProperty(value = "数据标准名称")
    @NotBlank
    @Size(max = 120)
    @ExcelColumn(zh = "标准名称", en = "standardName")
    private String standardName;

    @ApiModelProperty(value = "数据标准描述")
    @ExcelColumn(zh = "标准描述",en = "standardDesc")
    private String standardDesc;

    @ApiModelProperty(value = "数据类型 (XMOD.FIELD_TYPE)")
    @NotBlank
    @Size(max = 30)
    @LovValue(lovCode = "XMOD.FIELD_TYPE",meaningField = "dataTypeMeaning")
//    @ExcelColumn(zh = "数据类型",en="dataType")
    private String dataType;

    @ExcelColumn(zh = "数据类型",en="dataType")
    private String dataTypeMeaning;

    @ApiModelProperty(value = "数据格式")
    @ExcelColumn(zh = "数据格式",en = "dataPattern")
    private String dataPattern;

    @ApiModelProperty(value = "长度类型（快码HSDP.XSTA.LENGTH_TYPE）")
    @LovValue(lovCode = "XSTA.LENGTH_TYPE",meaningField = "lengthTypeMeaning")
//    @ExcelColumn(zh = "数据长度-类型",en ="lengthType")
    private String lengthType;

    @ExcelColumn(zh = "数据长度-类型",en ="lengthType")
    private String lengthTypeMeaning;

    @ApiModelProperty(value = "数据长度")
    @ExcelColumn(zh = "数据长度",en = "dataLength")
    private String dataLength;

    @ApiModelProperty(value = "值域类型（快码XSTA.VALUE_TYPE）")
    @LovValue(lovCode = "XSTA.VALUE_TYPE",meaningField = "valueTypeMeaning")
//    @ExcelColumn(zh = "值域类型",en="valueType")
    private String valueType;

    @ExcelColumn(zh = "值域类型",en="valueType")
    private String valueTypeMeaning;

    @ApiModelProperty(value = "值域")
    @ExcelColumn(zh = "值域范围",en = "valueRange")
    private String valueRange;

    @ApiModelProperty(value = "标准依据（快码XSTA.STANDARD_ACCORD）")
    @LovValue(lovCode = "XSTA.STANDARD_ACCORD",meaningField = "standardAccordMeaning")
//    @ExcelColumn(zh = "标准依据" ,en="standardAccord")
    private String standardAccord;

    @ExcelColumn(zh = "标准依据" ,en="standardAccord")
    private String standardAccordMeaning;

    @ApiModelProperty(value = "依据内容")
    @ExcelColumn(zh = "依据内容",en = "accordContent")
    private String accordContent;

    @ApiModelProperty(value = "是否可为空，1可空 0 不可空")
    @ExcelColumn(zh = "是否可为空",en = "Can be empty or not")
    private Integer nullFlag;

    @Transient
    @ExcelColumn(zh = "责任部门", en = "chargeDeptName")
    private String chargeDeptName;

    @Transient
    @ExcelColumn(zh = "责任人",en = "chargeName")
    private String chargeName;

    @ApiModelProperty(value = "责任人电话")
    @ExcelColumn(zh = "责任人电话",en = "chargeTel")
    private String chargeTel;

    @ExcelColumn(zh = "责任人邮箱",en = "chargeEmail")
    @ApiModelProperty(value = "责任人邮箱")
    private String chargeEmail;

    @ApiModelProperty(value = "责任部门ID")
    private Long chargeDeptId;

    @ApiModelProperty(value = "责任人ID")
    private Long chargeId;

    @ApiModelProperty(value = "数据标准状态(快码HSDP.XSTA.STANDARD_STATUS)")
    @NotBlank
    @Size(max = 30)
    private String standardStatus;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @Transient
    private List<StandardExtraDTO> standardExtraDTOList;

    @Transient
    private Long versionNumber;

    @Transient
    private String lastUpdateName;

    @Transient
    private Long parentGroupId;

    @Transient
    private String nameLevelPath;

    @Transient
    private List<Long> dataLengthList;

    @ApiModelProperty("查询条件，最后修订时间从")
    private String lastUpdateDateFrom;

    @ApiModelProperty("查询条件，最后修订时间至")
    private String lastUpdateDateTo;
    @Transient
    private String metadataName;

    @Transient
    private String metadataType;

    @Transient
    private String createdByName;

    @Transient
    private String lastUpdatedByName;

    private Long projectId;

    /**
     * 分组集合
     */
    @Transient
    private Long[] groupArrays;

    //用于勾选导出数据标准
    @Transient
    private String exportIds;

    @ApiModelProperty("发布人id")
    private Long releaseBy;

    @Transient
    private String releaseByName;

    @ApiModelProperty("发布时间")
    private Date releaseDate;

    @Transient
    private List<Long> standardIds;

    @Transient
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATETIME)
    private Date releaseDateFrom;

    @Transient
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATETIME)
    private Date releaseDateTo;

    @Transient
    private String referenceDataCode;

    @Transient
    private String referenceDataName;

}
