package org.xdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@ApiModel("简略参考数据值")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleReferenceDataValueDTO {

    @ApiModelProperty(value = "参考值")
    @NotBlank
    @Size(max = 128)
    private String value;

    @ApiModelProperty(value = "含义")
    @NotBlank
    @Size(max = 128)
    private String valueMeaning;

    @ApiModelProperty(value = "排序序列")
    @NotNull
    private Long valueSeq;

    @ApiModelProperty(value = "描述")
    private String valueDesc;

    @ApiModelProperty(value = "父参考值")
    private Long parentValueId;

    @ApiModelProperty(value = "父参考值")
    private String parentValue;

    @ApiModelProperty(value = "启用标志 1-启用 0-禁用")
    @NotNull
    private Integer enabledFlag;

}
