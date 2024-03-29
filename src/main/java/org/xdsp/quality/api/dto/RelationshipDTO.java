package org.xdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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

    @ApiModelProperty(value = "来源表字段名")
    @NotBlank
    @Size(max = 50)
    private String relFieldName;

    @ApiModelProperty(value = "关联关系 XQUA.REL_CODE")
    @NotBlank
    private String relCode;

    @ApiModelProperty(value = "目标表字段名")
    @NotBlank
    @Size(max = 50)
    private String baseFieldName;

    private Long projectId;


}
