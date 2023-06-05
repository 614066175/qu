package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * <p>标准附加信息表 数据传输对象</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("标准附加信息表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardExtraDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long extraId;

    @ApiModelProperty(value = "标准ID")
    @NotNull
    private Long standardId;

    @ApiModelProperty(value = "标准类型(快码：XSTA.STANDARD_TYPE：DATA/数据标准，FIELD/字段标准，NAME/命名标准)")
    @NotBlank
    @Size(max = 30)
    private String standardType;

    @ApiModelProperty(value = "额外信息键")
    @NotBlank
    @Size(max = 30)
    private String extraKey;

    @ApiModelProperty(value = "额外信息值")
    private String extraValue;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    private Long projectId;

}
