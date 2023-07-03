package org.xdsp.quality.api.dto;

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

/**
 * <p>参考数据值 数据传输对象</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("参考数据值")
@JsonInclude(JsonInclude.Include.NON_NULL)
@ExcelSheet(zh = "参考值", en = "Reference value", rowOffset = 2)
public class ReferenceDataValueDTO extends AuditDomain {

    @ApiModelProperty("参考值主键ID")
    private Long valueId;

    @ApiModelProperty(value = "参考数据头表ID")
    @NotNull
    private Long dataId;

    @ApiModelProperty(value = "参考值")
    @NotBlank
    @Size(max = 128)
    @ExcelColumn(zh = "参考值", en = "Value", order = 2)
    private String value;

    @ApiModelProperty(value = "含义")
    @NotBlank
    @Size(max = 128)
    @ExcelColumn(zh = "含义", en = "Meaning", order = 3)
    private String valueMeaning;

    @ApiModelProperty(value = "排序序列")
    @NotNull
    @ExcelColumn(zh = "排序", en = "seq", order = 4)
    private Long valueSeq;

    @ApiModelProperty(value = "描述")
    @ExcelColumn(zh = "描述", en = "Description", order = 5)
    private String valueDesc;

    @ApiModelProperty(value = "父参考值")
    private Long parentValueId;

    @ApiModelProperty(value = "启用标志 1-启用 0-禁用")
    @NotNull
    @ExcelColumn(zh = "是否启用", en = "Enabled flag", order = 7)
    private Integer enabledFlag;

    @ApiModelProperty(value = "项目ID")
    @NotNull
    private Long projectId;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "关键字搜索 参考值 含义的模糊")
    @Transient
    private String searchKey;

    @ApiModelProperty(value = "参考数据编码")
    @Transient
    @ExcelColumn(zh = "参考数据", en = "Reference data", order = 1)
    private String dataCode;

    @ApiModelProperty(value = "父参考数据")
    @Transient
    @ExcelColumn(zh = "父参考值", en = "Parent value", order = 6)
    private String parentValue;
}
