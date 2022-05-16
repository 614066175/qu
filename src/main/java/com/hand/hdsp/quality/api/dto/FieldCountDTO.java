package com.hand.hdsp.quality.api.dto;

/**
 * <p>字段落标统计 传输对象<p>
 *
 * @author lizheng  2022-05-12 10:05
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hzero.export.annotation.ExcelSheet;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("字段落标统计")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldCountDTO {


    @ApiModelProperty(value = "表类型")
    private String tableType;

    @ApiModelProperty(value = "数据源类型")
    @NotBlank
    @Size(max = 30)
    private String datasourceType;

    @ApiModelProperty(value = "表名称")
    @NotBlank
    @Size(max = 120)
    private String tableName;

    @ApiModelProperty(value = "表描述")
    private String tableDesc;

    @ApiModelProperty(value = "表字段描述")
    private String tableFieldDesc;

    @ApiModelProperty(value = "行数")
    private Long row;

    @ApiModelProperty(value = "非空行数")
    private Long nonNullRow;

    @ApiModelProperty(value = "合规行数")
    private Long compliantRow;

    @ApiModelProperty(value = "总行合规比例")
    private Float rate;

    @ApiModelProperty(value = "非空行合规比例")
    private Float blankRowRate;








}
