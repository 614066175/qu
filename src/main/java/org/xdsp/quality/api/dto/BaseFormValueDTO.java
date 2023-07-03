package org.xdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * <p>质检项表单值 数据传输对象</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-20 15:36:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("质检项表单值")
@JsonInclude(JsonInclude.Include.NON_NULL)
@ExcelSheet(zh = "质检项表单值", en = "form value table")
public class BaseFormValueDTO extends AuditDomain {

    @ApiModelProperty(value = "质检项编码")
    @ExcelColumn(zh = "质检项编码", en = "plan base code")
    @Transient
    private String planBaseCode;

    @ApiModelProperty("主键")
    private Long relationId;

    @ApiModelProperty(value = "质检项id")
    @NotNull
    private Long planBaseId;

    @ApiModelProperty(value = "表单行id")
    @NotNull
    private Long formLineId;

    @ApiModelProperty(value = "表单值")
    @ExcelColumn(zh = "表单值", en = "form value")
    private String formValue;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;


    @ApiModelProperty(value = "项目id")
    @NotNull
    private Long projectId;

    @Transient
    @ExcelColumn(zh = "表单编码", en = "item code")
    private String itemCode;

}
