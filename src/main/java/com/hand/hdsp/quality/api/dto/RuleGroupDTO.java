package com.hand.hdsp.quality.api.dto;

import java.util.List;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

/**
 * 规则分组表 数据传输对象
 *
 * @author wuzhong26857
 * @version 1.0
 * @date 2020/3/23 12:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("规则分组表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@VersionAudit
@ModifyAudit
@ExcelSheet(zh = "规则分组",en = "rule group")
public class RuleGroupDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    private Long groupId;

    @ApiModelProperty("父级分组ID")
    private Long parentGroupId;

    @ApiModelProperty(value = "分组编码")
    @NotBlank
    @Size(max = 50)
    @ExcelColumn(zh = "分组编码",en = "group code",showInChildren = true)
    private String groupCode;

    @Transient
    @ExcelColumn(zh = "父分组编码",en = "parentGroupCode")
    private String parentGroupCode;

    @ApiModelProperty(value = "分组名称")
    @NotBlank
    @ExcelColumn(zh = "分组名称",en = "group name")
    private String groupName;

    @ApiModelProperty(value = "分组描述")
    @ExcelColumn(zh = "分组描述",en = "group desc")
    private String groupDesc;

    /**
     * 标准规则列表
     */
    @Transient
    @ExcelColumn(zh = "标准规则列表", en = "ruleDTOList", child = true)
    private List<RuleDTO> ruleDTOList;



    /**
     *字段标准列表
     */
    @Transient
    @ExcelColumn(zh = "字段标准列表",en = "dataFieldDTOList",child = true)
    private List<DataFieldDTO> dataFieldDTOList;

    /**
     *命名标准列表
     */
    @Transient
    @ExcelColumn(zh = "命名标准列表",en = "nameStandardDTOList",child = true)
    private List<NameStandardDTO> nameStandardDTOList;

    /**
     *标准文档列表
     */
    @Transient
    @ExcelColumn(zh = "标准文档列表",en = "standardDocDTOList",child = true)
    private List<StandardDocDTO> standardDocDTOList;


    /**
     *批处理评估方案列表
     */
    @Transient
    @ExcelColumn(zh = "批处理评估方案列表",en = "batchPlanDTOList",child = true)
    private List<BatchPlanDTO> batchPlanDTOList;

    /**
     *批处理评估方案列表
     */
    @Transient
    @ExcelColumn(zh = "流处理评估方案列表",en = "streamingPlanDTOList",child = true)
    private List<StreamingPlanDTO> streamingPlanDTOList;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    private Long projectId;
}
