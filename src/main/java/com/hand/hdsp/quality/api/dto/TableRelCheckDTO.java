package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/12/11 17:36
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("表间计算值比较规则")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TableRelCheckDTO {


    private String relFunction;

    private String relFieldName;

    @ApiModelProperty(value = "关联关系 XQUA.REL_CODE")
    @NotBlank
    private String relCode;

    private String baseFunction;

    private String baseFieldName;



}
