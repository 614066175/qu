package org.xdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * <p>标准-标准组关系表 数据传输对象</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-09 15:50:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("标准-标准组关系表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardRelationDTO extends AuditDomain {

    @ApiModelProperty("关系id")
    private Long relationId;

    @ApiModelProperty(value = "字段标准id")
    @NotNull
    private Long fieldStandardId;

    @ApiModelProperty(value = "标准组id")
    @NotNull
    private Long standardTeamId;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "项目id")
    @NotNull
    private Long projectId;

}
