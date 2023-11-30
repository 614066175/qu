package org.xdsp.quality.api.dto;

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
@ExcelSheet(zh = "字段标准", en = "Field Standard",rowOffset = 2)
public class DataFieldDTO extends AuditDomain {

    @Transient
//    @ExcelColumn(zh = "分组名称", en = "groupName", groups = {Group1.class})
    private String groupName;

    @Transient
    @ExcelColumn(zh = "分组全路径", en = "groupPath", order = 0)
    private String groupPath;

    @Transient
//    @ExcelColumn(zh = "分组编码", en = "groupCode", groups = {Group1.class})
    private String groupCode;

    @Transient
//    @ExcelColumn(zh = "分组描述", en = "groupDesc", groups = {Group1.class})
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
    @ExcelColumn(zh = "字段名称", en = "fieldName", order = 10)
    private String fieldName;

    @ApiModelProperty(value = "字段注释")
    @NotBlank
    @Size(max = 255)
    @ExcelColumn(zh = "字段注释", en = "fieldComment", order = 20)
    private String fieldComment;

    @ApiModelProperty(value = "标准描述")
    @ExcelColumn(zh = "标准描述", en = "standardDesc", order = 30)
    private String standardDesc;

    //字段标准组：支持填写多个值，可用;区分
    @Transient
    @ExcelColumn(zh = "字段标准组", en = "standardTeamCode", order = 40)
    private String standardTeamCode;

    //引用数据标准
    @Transient
    @ExcelColumn(zh = "引用数据标准",en = "dataStandardCode", order = 50)
    private String dataStandardCode;

    @ApiModelProperty(value = "字段类型 (XMOD.FIELD_TYPE)")
    @NotBlank
    @LovValue(lovCode = "XMOD.FIELD_TYPE",meaningField = "fieldTypeMeaning")
//    @ExcelColumn(zh = "字段类型", en = "fieldType", groups = {Group1.class})
    private String fieldType;

    //字段精度
    @ApiModelProperty(value = "字段精度")
    @ExcelColumn(zh = "字段精度", en = "fieldAccuracy", order = 70)
    private Integer fieldAccuracy;

    @ExcelColumn(zh = "字段类型", en = "fieldType", order = 60)
    private String fieldTypeMeaning;

    @ApiModelProperty(value = "字段长度")
    @ExcelColumn(zh = "字段长度", en = "fieldLength", order = 80)
    private String fieldLength;

    @ApiModelProperty(value = "数据格式")
    @ExcelColumn(zh = "数据格式", en = "dataPattern", order = 90)
    private String dataPattern;

    @ApiModelProperty(value = "值域类型（快码XSTA.VALUE_TYPE）")
    @LovValue(lovCode = "XSTA.VALUE_TYPE",meaningField = "valueTypeMeaning")
//    @ExcelColumn(zh = "值域类型", en = "valueType", groups = {Group1.class})
    private String valueType;

    @ExcelColumn(zh = "值域类型", en = "valueType", order = 100)
    private String valueTypeMeaning;

    @ApiModelProperty(value = "值域范围")
    @ExcelColumn(zh = "值域范围", en = "valueRange", order = 110)
    private String valueRange;

    @ApiModelProperty(value = "是否可为空，1可空 0 不可空")
    @ExcelColumn(zh = "是否可为空" ,en = "Can be empty or not", order = 120)
    private Integer nullFlag;

    //默认值
    @ApiModelProperty(value = "默认值")
    @ExcelColumn(zh = "默认值" ,en = "defaultValue", order = 130)
    private String defaultValue;

    //系统常用名
    @ApiModelProperty(value = "系统常用名")
    @ExcelColumn(zh = "系统常用名", en = "sysCommonName", order = 140)
    private String sysCommonName;

    @Transient
    @ExcelColumn(zh = "责任部门", en = "chargeDeptName", order = 200)
    private String chargeDeptName;

    @Transient
    @ExcelColumn(zh = "责任人", en = "chargeName", order = 210)
    private String chargeName;

    @ApiModelProperty(value = "责任人电话")
    @ExcelColumn(zh = "责任人电话", en = "chargeTel", order = 220)
    private String chargeTel;

    @ApiModelProperty(value = "责任人邮箱")
    @ExcelColumn(zh = "责任人邮箱", en = "chargeEmail", order = 230)
    private String chargeEmail;

    @ApiModelProperty(value = "责任部门ID")
    @NotNull
    private Long chargeDeptId;

    @ApiModelProperty(value = "责任人ID")
    @NotNull
    private Long chargeId;

    @ApiModelProperty(value = "字段标准状态(快码HSDP.XSTA.STANDARD_STATUS)")
    @NotBlank
    @Size(max = 50)
    private String standardStatus;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @Transient
    private List<StandardExtraDTO> standardExtraDTOList;

    @ExcelColumn(zh = "附加信息",en = "extra", order = 240)
    @Transient
    private String standardExtraStr;

    @Transient
    private Long versionNumber;

    @Transient
    private String lastUpdateName;

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

    @Transient
    private Long instanceId;

    /**
     * 分组集合
     */
    @Transient
    private Long[] groupArrays;

    /**
     * 关联的标准组code，逗号分隔
     */
    @Transient
    private String standardTeamCodes;

    //用于勾选导出字段标准
    @Transient
    private String exportIds;

    @Transient
    private List<Long> fieldIds;

    public interface Group1 {
    }

    public interface Group2 {
    }

    @ApiModelProperty("发布人id")
    private Long releaseBy;

    @Transient
    private String releaseByName;

    @ApiModelProperty("发布时间")
    private Date releaseDate;

    @ApiModelProperty(value = "业务用途")
    @ExcelColumn(zh = "业务用途", en = "businessPurpose", order = 150)
    private String businessPurpose;

    @ApiModelProperty(value = "业务规则")
    @ExcelColumn(zh = "业务规则", en = "businessRules", order = 160)
    private String businessRules;

    @ApiModelProperty(value = "数据示例")
    @ExcelColumn(zh = "数据示例", en = "dataExample", order = 170)
    private String dataExample;

    @ApiModelProperty(value = "标准依据")
    @ExcelColumn(zh = "标准依据", en = "standardAccord", order = 180)
    private String standardAccord;

    @ApiModelProperty(value = "依据内容")
    @ExcelColumn(zh = "依据内容", en = "accordContent", order = 190)
    private String accordContent;


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
