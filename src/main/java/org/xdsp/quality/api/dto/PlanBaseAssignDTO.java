package org.xdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * <p>质检项分配表 数据传输对象</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-20 15:36:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("质检项分配表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlanBaseAssignDTO extends AuditDomain {

    @ApiModelProperty("主键，质检项分配id")
    private Long baseAssignId;

    @ApiModelProperty(value = "质检项id")
    @NotNull
    private Long planBaseId;

    @ApiModelProperty(value = "评估方案id")
    private Long planId;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;


    @ApiModelProperty(value = "项目id")
    @NotNull
    private Long projectId;

}
