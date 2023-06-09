package org.xdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * <p>落标包含表 数据传输对象</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("落标包含表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NameAimIncludeDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long includeId;

    @ApiModelProperty(value = "落标ID，关联表XSTA_NAME_AIM")
    @NotNull
    private Long aimId;

    @ApiModelProperty(value = "数据库名称")
    @NotBlank
    @Size(max = 120)
    private String schemaName;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    //===============================================================================
    //  说明: 业务字段
    //===============================================================================

    /**
     * 该包含项中排除的表
     */
    @Transient
    private List<String> excludeTables;

    private Long projectId;
}
