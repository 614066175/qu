package org.xdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hzero.export.annotation.ExcelColumn;
import org.xdsp.quality.domain.entity.Root;

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
public class RootGroupDTO extends AuditDomain {

    @ApiModelProperty("分组ID，主键，供其他表做外键")
    private Long groupId;

    @ApiModelProperty(value = "父分组ID")
    @NotNull
    private Long parentGroupId;

    @ApiModelProperty(value = "分组名称")
    @NotBlank
    @Size(max = 120)
    @ExcelColumn(zh = "分组名称",en = "groupName")
    private String groupName;

    @ApiModelProperty(value = "父分组路径")
    @ExcelColumn(zh = "父分组路径",en = "parentGroupPath")
    private String parentGroupPath;

    @ApiModelProperty(value = "分组路径")
    private String groupPath;

    @ApiModelProperty(value = "分组类型")
    @NotBlank
    @Size(max = 30)
    private String groupType;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "项目ID")
    @NotNull
    private Long projectId;

    @Transient
    @ExcelColumn(zh = "数据标准列表", en = "rootDTOS", child = true)
    private List<Root> roots;

    /**
     * 分组层级，用于导出分组时排序
     */
    @Transient
    private Integer groupLevel;
}
