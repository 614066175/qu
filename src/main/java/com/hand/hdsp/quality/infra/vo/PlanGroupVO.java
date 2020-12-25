package com.hand.hdsp.quality.infra.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hzero.export.annotation.ExcelColumn;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/21 17:26
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@VersionAudit
@ModifyAudit
public class PlanGroupVO {

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
}
