package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ExcelSheet(zh = "分组",en = "NameStandard Group",rowOffset = 3)
public class NameStandardGroupDTO extends AuditDomain {

    @ApiModelProperty("分组ID，主键，供其他表做外键")
    private Long groupId;

    @ApiModelProperty(value = "父分组ID")
    @NotNull
    private Long parentGroupId;

    @ApiModelProperty(value = "分组编码")
    @NotBlank
    @Size(max = 50)
    @ExcelColumn(zh = "分组编码",en = "groupCode", showInChildren = true)
    private String groupCode;

    @ApiModelProperty(value = "分组名称")
    @NotBlank
    @Size(max = 120)
    @ExcelColumn(zh = "分组名称",en = "groupName")
    private String groupName;

    @ApiModelProperty(value = "分组描述")
    @ExcelColumn(zh = "分组描述",en = "groupDesc")
    private String groupDesc;

    @ExcelColumn(zh = "父分组编码",en = "parentGroupCode")
    @Transient
    private String parentGroupCode;

    @ApiModelProperty(value = "标准类型(快码：HDSP.XSTA.STANDARD_TYPE：DATA/数据标准，FIELD/字段标准，NAME/命名标准)")
    @NotBlank
    @Size(max = 30)
    private String standardType;

    @ApiModelProperty(value = "是否启用 1-启用 0-不启用")
    @NotNull
    private Integer enabledFlag;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @Transient
    @ExcelColumn(zh = "命名标准列表", en = "nameStandardDTOList", child = true)
    private List<NameStandardDTO> nameStandardDTOList;

    @Transient
    private int groupLevel;

    private Long projectId;
}
