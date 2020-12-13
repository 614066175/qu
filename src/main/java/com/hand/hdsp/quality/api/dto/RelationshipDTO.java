package com.hand.hdsp.quality.api.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p>批数据方案-表间规则关联关系表 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("表间规则关联关系")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RelationshipDTO extends AuditDomain {

    @ApiModelProperty(value = "源表字段名")
    @NotBlank
    @Size(max = 50)
    private String sourceFieldName;

    @ApiModelProperty(value = "关联关系 HDSP.XQUA.REL_CODE")
    @NotBlank
    private String relCode;

    @ApiModelProperty(value = "关联表字段名")
    @NotBlank
    @Size(max = 50)
    private String relFieldName;


    private TableRelCheckDTO tableRelCheckDTO;
}
