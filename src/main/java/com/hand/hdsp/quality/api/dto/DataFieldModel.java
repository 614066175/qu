package com.hand.hdsp.quality.api.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataFieldModel extends BaseRowModel {

    @ApiModelProperty(value = "字段注释")
    @ExcelProperty(value = "字段注释",index = 0)
    private String fieldComment;

    @ApiModelProperty(value = "字段名称")
    @ExcelProperty(value = "字段名称",index = 1)
    private String fieldName;

    @ExcelProperty(value = "分组名称",index = 2)
    private String groupName;

    @ApiModelProperty(value = "标准描述")
    @ExcelProperty(value = "标准描述",index = 3)
    private String standardDesc;

    //字段标准组：支持填写多个值，可用;区分
    @ExcelProperty(value = "字段标准组",index = 4)
    private String standardTeamCode;

    @ExcelProperty(value = "引用数据标准",index = 5)
    private String dataStandardCode;

    @ApiModelProperty(value = "字段精度")
    @ExcelProperty(value = "字段精度",index = 6)
    private Integer fieldAccuracy;

    @ApiModelProperty(value = "字段类型 (HDSP.XMOD.FIELD_TYPE)")
    @ExcelProperty(value = "字段类型",index = 7)
    private String fieldType;

    @ApiModelProperty(value = "字段长度")
    @ExcelProperty(value = "字段长度",index = 8)
    private String fieldLength;

    @ApiModelProperty(value = "数据格式")
    @ExcelProperty(value = "数据格式",index = 9)
    private String dataPattern;

    @ApiModelProperty(value = "值域类型（快码HDSP.XSTA.VALUE_TYPE）")
    @ExcelProperty(value = "值域类型",index = 10)
    private String valueType;

    @ApiModelProperty(value = "值域范围")
    @ExcelProperty(value = "值域范围",index = 11)
    private String valueRange;

    @ApiModelProperty(value = "是否可为空，1可空 0 不可空")
    @ExcelProperty(value = "是否可为空",index = 12)
    private Integer nullFlag;

    @ApiModelProperty(value = "默认值")
    @ExcelProperty(value = "默认值",index = 13)
    private String defaultValue;

    @ApiModelProperty(value = "系统常用名")
    @ExcelProperty(value = "系统常用名",index = 14)
    private String sysCommonName;

    @ExcelProperty(value = "责任部门",index = 15)
    private String chargeDeptName;

    @ExcelProperty(value = "责任人",index = 16)
    private String chargeName;

    @ApiModelProperty(value = "责任人电话")
    @ExcelProperty(value = "责任人电话",index = 17)
    private String chargeTel;

    @ApiModelProperty(value = "责任人邮箱")
    @ExcelProperty(value = "责任人邮箱",index = 18)
    private String chargeEmail;

    @ExcelProperty(value = "字段类型",index = 19)
    private String fieldTypeMeaning;

    /**
     * 关联的标准组code，逗号分隔
     */
    @Transient
    private String standardTeamCodes;

}
