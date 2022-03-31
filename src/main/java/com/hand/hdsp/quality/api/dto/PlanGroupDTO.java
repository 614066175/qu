package com.hand.hdsp.quality.api.dto;

import java.util.List;
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
 * <p>评估方案分组表 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("评估方案分组表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@VersionAudit
@ModifyAudit
@ExcelSheet(zh = "评估方案分组",en = "plan group")
public class PlanGroupDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long groupId;

    @ApiModelProperty(value = "父级分组ID")
    @NotNull
    private Long parentGroupId;

    @ApiModelProperty(value = "分组编码")
    @NotBlank
    @Size(max = 50)
    @ExcelColumn(zh = "分组编码",en = "group code" ,showInChildren = true)
    private String groupCode;

    @ApiModelProperty(value = "分组名称")
    @NotBlank
    @Size(max = 255)
    @ExcelColumn(zh = "分组名称",en = "group name")
    private String groupName;

    @ApiModelProperty(value = "分组描述")
    @ExcelColumn(zh = "分组描述",en ="group desc")
    private String groupDesc;

    @ApiModelProperty(value = "分组类型BATCH/STREAMING")
    @ExcelColumn(zh="分组类型", en = "group type")
    private String groupType;

    @ApiModelProperty(value = "是否启用 1-启用 0-不启用")
    private Integer enabledFlag;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @Transient
    @ExcelColumn(zh = "评估方案列表", en = "batchPlanDTOList", child = true)
    private List<BatchPlanDTO> batchPlanDTOList;

    private Long projectId;

}
