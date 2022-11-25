package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * description
 *
 * @author XIN.SHENG01@HAND-CHINA.COM 2022/11/22 15:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@VersionAudit
@ModifyAudit
public class RootLineDTO extends AuditDomain {

    @Id
    private Long rootLineId;
    @ApiModelProperty(value = "root主键",required = true)
    @NotNull
    private Long rootId;
    @ApiModelProperty(value = "词根中文名",required = true)
    @NotBlank
    private String rootName;
    @ApiModelProperty(value = "租户ID",required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "项目ID",required = true)
    @NotNull
    private Long projectId;
}
