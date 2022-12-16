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
 * <p> 数据传输对象</p>
 *
 * @author guoliang.li01@hand-china.com 2022-12-06 14:35:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RootDicDTO extends AuditDomain {

    @ApiModelProperty("")
    private Long id;

    @ApiModelProperty(value = "词库文件名")
    @NotBlank
    @Size(max = 255)
    private String dicName;

    @ApiModelProperty(value = "dic词库路径")
    @NotBlank
    @Size(max = 220)
    private String dicUrl;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "项目id")
    @NotNull
    private Long projectId;

}
