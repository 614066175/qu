package com.hand.hdsp.quality.api.dto;

import java.util.List;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

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
@ExcelSheet(zh = "数据标准", en = "Data Standard")
public class DataStandardDTO extends AuditDomain{

    @ApiModelProperty("数据标准ID，主键，供其他表做外键")
    private Long standardId;

    @ApiModelProperty(value = "分组ID")
    @NotNull
    private Long groupId;

    @ApiModelProperty(value = "数据标准编码")
    @NotBlank
    @Size(max = 50)
    @ExcelColumn(zh = "标准编码", en = "standardCode" ,groups = {Group1.class})
    private String standardCode;

    @ApiModelProperty(value = "数据标准名称")
    @NotBlank
    @Size(max = 120)
    @ExcelColumn(zh = "标准名称", en = "standardName",groups = {Group1.class} )
    private String standardName;

    @ApiModelProperty(value = "数据标准描述")
    @ExcelColumn(zh = "标准描述",en = "standardDesc",groups = {Group1.class})
    private String standardDesc;

    @ApiModelProperty(value = "数据类型 (HDSP.XDMP.LABEL_DATA_TYPE)")
    @NotBlank
    @Size(max = 30)
    @ExcelColumn(zh = "数据类型",en="dataType",groups = {Group1.class})
    private String dataType;

    @ApiModelProperty(value = "数据格式")
    @ExcelColumn(zh = "数据格式",en = "dataPattern",groups = {Group1.class})
    private String dataPattern;

    @ApiModelProperty(value = "长度类型（快码HSDP.XSTA.LENGTH_TYPE）")
    @ExcelColumn(zh = "数据长度-类型",en ="lengthType",groups = {Group1.class})
    private String lengthType;

    @ApiModelProperty(value = "数据长度")
    @ExcelColumn(zh = "数据长度",en = "dataLength",groups = {Group1.class})
    private String dataLength;

    @ApiModelProperty(value = "值域类型（快码HDSP.XSTA.VALUE_TYPE）")
    @ExcelColumn(zh = "值域类型",en="valueType",groups = {Group1.class})
    private String valueType;

    @ApiModelProperty(value = "值域")
    @ExcelColumn(zh = "值域范围",en = "valueRange",groups = {Group1.class})
    private String valueRange;

    @ApiModelProperty(value = "标准依据（快码HDSP.XSTA.STANDARD_ACCORD）")
    @ExcelColumn(zh = "标准依据" ,en="standardAccord",groups = {Group1.class})
    private String standardAccord;

    @ApiModelProperty(value = "依据内容")
    @ExcelColumn(zh = "依据内容",en = "accordContent",groups = {Group1.class})
    private String accordContent;

    @ApiModelProperty(value = "责任部门ID")
    @NotNull
    private Long chargeDeptId;

    @ApiModelProperty(value = "责任人ID")
    @NotNull
    private Long chargeId;

    @ApiModelProperty(value = "责任人电话")
    @ExcelColumn(zh = "责任人电话",en = "chargeTel",groups = {Group2.class})
    private String chargeTel;

    @ExcelColumn(zh = "责任人邮箱",en = "chargeEmail",groups = {Group2.class})
    @ApiModelProperty(value = "责任人邮箱")
    private String chargeEmail;

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
    @ExcelColumn(zh = "责任人",en = "chargeName",groups = {Group2.class})
    private String chargeName;

    @Transient
    @ExcelColumn(zh = "责任部门", en = "chargeDeptName",groups = {Group2.class})
    private String chargeDeptName;

    @Transient
    private String groupName;

    @Transient
    private String groupCode;

    @Transient
    private String groupDesc;

    @Transient
    private List<Long> dataLengthList;

    public interface Group1 {

    }
    public interface Group2 {}
}
