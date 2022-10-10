package com.hand.hdsp.quality.api.dto;

import java.util.Date;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

/**
 * <p>命名标准表 数据传输对象</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("命名标准表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@ExcelSheet(zh = "命名标准", en = "Name Standard")
public class NameStandardDTO extends AuditDomain {

    @ApiModelProperty("命名标准ID，主键，供其他表做外键")
    private Long standardId;

    @ApiModelProperty(value = "分组ID，关联表XSTA_STANDARD_GROUP")
    @NotNull
    private Long groupId;

    @ExcelColumn(zh = "标准编码",en="standardCode",order = 3)
    @ApiModelProperty(value = "命名标准编码")
    @NotBlank
    @Size(max = 80)
    private String standardCode;

    @ExcelColumn(zh = "命名标准名称",en="standardName",order = 4)
    @ApiModelProperty(value = "命名标准名称")
    @NotBlank
    @Size(max = 120)
    private String standardName;

    @ExcelColumn(zh = "命名标准描述",en="standardDesc",order = 5)
    @ApiModelProperty(value = "命名标准描述")
    private String standardDesc;

    @ExcelColumn(zh = "命名标准类型",en="standardType",order = 6)
    @LovValue(lovCode = "HDSP.XSTA.NAME_STANDARD_TYPE",meaningField = "standardTypeMeaning")
    @ApiModelProperty(value = "命名标准类型，快码：HDSP.XSTA.NAME_STANDARD_TYPE <TABLE:表名称>")
    @NotBlank
    @Size(max = 30)
    private String standardType;

    @ExcelColumn(zh = "命名标准规则",en="standardRule",order = 7)
    @ApiModelProperty(value = "命名标准规则")
    @NotBlank
    @Size(max = 240)
    private String standardRule;

    @ExcelColumn(zh = "是否忽略大小写",en="ignoreCaseFlag",order = 8)
    @ApiModelProperty(value = "是否忽略大小写 1-忽略 0-不忽略")
    @NotNull
    private Integer ignoreCaseFlag;

    @ExcelColumn(zh = "责任人电话",en="chargeTel",order = 11)
    @ApiModelProperty(value = "责任人电话")
    private String chargeTel;

    @ExcelColumn(zh = "责任人邮箱",en="chargeEmail",order = 12)
    @ApiModelProperty(value = "责任人邮箱")
    private String chargeEmail;

    @ApiModelProperty(value = "责任人ID")
    @NotNull
    private Long chargeId;

    @ApiModelProperty(value = "责任部门ID")
    private Long chargeDeptId;

    @LovValue(lovCode = "HDSP.XSTA.EXEC_STATUS",meaningField = "latestCheckedStatusMeaning")
    @ApiModelProperty(value = "最新稽核状态，值集：HDSP.XSTA.EXEC_STATUS")
    private String latestCheckedStatus;

    @ApiModelProperty("最新稽核异常数量")
    private Long latestAbnormalNum;

    @ApiModelProperty(value = "是否启用 1-启用 0-不启用")
    private Integer enabledFlag;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    //===============================================================================
    //  说明：业务字段
    //===============================================================================

    /**
     * 最近修订人姓名
     */
    @Transient
    private String lastUpdateName;


    /**
     * 标准类型值集意义
     */
    @Transient
    private String standardTypeMeaning;

    /**
     * 执行状态意义
     */
    @Transient
    private String latestCheckedStatusMeaning;

    /**
     * 责任人姓名
     */
    @ExcelColumn(zh = "责任人姓名",en="chargeName",order = 10)
    @Transient
    private String chargeName;

    /**
     * 责任部门名称
     */
    @ExcelColumn(zh = "责任部门",en="chargeDeptName",order = 9)
    @Transient
    private String chargeDeptName;

    /**
     * 分组编码
     */
//    @ExcelColumn(zh = "分组编码",en="groupCode",order = 2)
    @Transient
    private String groupCode;

    @Transient
    private String groupName;

    @Transient
    private Date lastUpdateDateFrom;

    @Transient
    private Date lastUpdateDateTo;

    @Transient
    private String createdByName;

    @Transient
    private String lastUpdatedByName;

    private Long projectId;

}
