package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hand.hdsp.quality.domain.entity.Root;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.choerodon.mybatis.domain.AuditDomain;

import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ExcelSheet(zh = "分组",en = "Root Group",rowOffset = 2)
public class RootGroupDTO extends AuditDomain {

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

    @Transient
    @ExcelColumn(zh = "父分组编码",en = "parentGroupCode")
    private String 	parentGroupCode;

    @ApiModelProperty(value = "标准类型(快码：HDSP.XSTA.STANDARD_TYPE：DATA/数据标准，FIELD/字段标准，NAME/命名标准,ROOT/词根)")
    @NotBlank
    @Size(max = 30)
    private String standardType;

    @ApiModelProperty(value = "是否启用 1-启用 0-不启用")
    @NotNull
    private Integer enabledFlag;

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
    private int groupLevel;
}
