package org.xdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * <p>字段标准匹配表 数据传输对象</p>
 *
 * @author shijie.gao@hand-china.com 2022-12-07 10:42:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("字段标准匹配表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RootMatchDTO extends AuditDomain {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty(value = "批次号")
    @NotBlank
    @Size(max = 120)
    private String batchNumber;

    @ApiModelProperty(value = "字段注释")
    @NotBlank
    @Size(max = 120)
    private String fieldComment;

    @ApiModelProperty(value = "匹配状态:( 匹配中,匹配成功,匹配失败)")
    @NotBlank
    @Size(max = 120)
    private String matchingStatus;

    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    @ApiModelProperty(value = "来源：(词根翻译 , 字段标准)")
    private String source;

    @ApiModelProperty(value = "字段标准id")
    private Long fieldId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "租户id")
    private Long tenantId;

    @Transient
    private Integer reMatchFlag;

    //导出类型 excel/csv
    @Transient
    private String exportType;

    //字段描述
    @Transient
    private String fieldDescription;

    //字段类型
    @Transient
    private String fieldType;
}
