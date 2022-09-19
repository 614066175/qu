package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
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
@ExcelSheet(zh = "字段标准", en = "Field Standard")
public class DataFieldDTO extends AuditDomain {

    @Transient
    @ExcelColumn(zh = "分组名称", en = "groupName", groups = {Group1.class})
    private String groupName;

    @Transient
    @ExcelColumn(zh = "分组编码", en = "groupCode", groups = {Group1.class})
    private String groupCode;

    @Transient
    @ExcelColumn(zh = "分组描述", en = "groupDesc", groups = {Group1.class})
    private String groupDesc;

    @ApiModelProperty("字段标准ID，主键，供其他表做外键")
    private Long fieldId;

    @ApiModelProperty("数据标准ID，主键，供其他表做外键")
    private Long dataStandardId;

    @ApiModelProperty(value = "分组ID")
    @NotNull
    private Long groupId;

    @ApiModelProperty(value = "字段名称")
    @NotBlank
    @Size(max = 255)
    @ExcelColumn(zh = "字段名称", en = "fieldName", groups = {Group1.class})
    private String fieldName;

    @ApiModelProperty(value = "字段注释")
    @NotBlank
    @Size(max = 255)
    @ExcelColumn(zh = "字段注释", en = "fieldComment", groups = {Group1.class})
    private String fieldComment;

    @ApiModelProperty(value = "系统常用名")
    @ExcelColumn(zh = "标准常用名", en = "sysCommonName", groups = {Group1.class})
    private String sysCommonName;

    @ApiModelProperty(value = "标准描述")
    @ExcelColumn(zh = "标准描述", en = "standardDesc", groups = {Group1.class})
    private String standardDesc;

    @ApiModelProperty(value = "字段类型 (HDSP.XMOD.FIELD_TYPE)")
    @NotBlank
    @ExcelColumn(zh = "字段类型", en = "fieldType", groups = {Group1.class})
    private String fieldType;

    @ApiModelProperty(value = "字段长度")
    @ExcelColumn(zh = "字段长度", en = "fieldLength", groups = {Group1.class})
    private String fieldLength;

    @ApiModelProperty(value = "数据格式")
    @ExcelColumn(zh = "数据格式", en = "dataPattern", groups = {Group1.class})
    private String dataPattern;

    @ApiModelProperty(value = "值域类型（快码HDSP.XSTA.VALUE_TYPE）")
    @ExcelColumn(zh = "值域类型", en = "valueType", groups = {Group1.class})
    private String valueType;

    @ApiModelProperty(value = "值域范围")
    @ExcelColumn(zh = "值域范围", en = "valueRange", groups = {Group1.class})
    private String valueRange;

    @ApiModelProperty(value = "责任部门ID")
    @NotNull
    private Long chargeDeptId;

    @ApiModelProperty(value = "责任人ID")
    @NotNull
    private Long chargeId;

    @ApiModelProperty(value = "责任人电话")
    @ExcelColumn(zh = "责任人电话", en = "chargeTel", groups = {Group2.class})
    private String chargeTel;

    @ApiModelProperty(value = "责任人邮箱")
    @ExcelColumn(zh = "责任人邮箱", en = "chargeEmail", groups = {Group2.class})
    private String chargeEmail;

    @ApiModelProperty(value = "字段标准状态(快码HSDP.XSTA.STANDARD_STATUS)")
    @NotBlank
    @Size(max = 50)
    private String standardStatus;

    @ApiModelProperty(value = "字段精度")
    private Integer fieldAccuracy;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "是否可为空，1可空 0 不可空")
    @ExcelColumn(zh = "是否可为空" ,en = "Can be empty or not")
    private Integer nullFlag;

    @Transient
    private List<StandardExtraDTO> standardExtraDTOList;

    @Transient
    private Long versionNumber;

    @Transient
    private String lastUpdateName;

    @Transient
    @ExcelColumn(zh = "责任人", en = "chargeName", groups = {Group2.class})
    private String chargeName;

    @Transient
    @ExcelColumn(zh = "责任部门", en = "chargeDeptName", groups = {Group2.class})
    private String chargeDeptName;

    @ApiModelProperty(value = "默认值")
    private String defaultValue;


    @Transient
    private List<Long> dataLengthList;

    @Transient
    private Date lastUpdateDateFrom;

    @Transient
    private Date lastUpdateDateTo;


    @Transient
    private String createdByName;

    @Transient
    private String lastUpdatedByName;

    @Transient
    private String dataStandardName;

    private Long projectId;


    //标准组id
    @Transient
    private Long standardTeamId;

    //继承自标准组id
    @Transient
    private Long inheriteTeamId;


    //是否选中，即是否是此标准组下标准（0否，1是）
    @Transient
    private Integer checkFlag = 0;

    //是否编辑标识，继承的标准组下的标准不允许编辑（0否，1时）
    @Transient
    private Integer editFlag = 1;


    //标准组
    @Transient
    private List<StandardTeamDTO> standardTeamDTOList;

    @Transient
    //用于排除显示
    private List<Long> checkFieldIds;

    @Transient
    //表设计主键，用于排除表设计已保存字段引用的字段标准
    private Long customTableId;

    public interface Group1 {
    }

    public interface Group2 {
    }
}
