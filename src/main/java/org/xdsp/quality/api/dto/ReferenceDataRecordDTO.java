package org.xdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * <p>参考数据工作流记录表 数据传输对象</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-17 20:01:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("参考数据工作流记录表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReferenceDataRecordDTO extends AuditDomain {

    @ApiModelProperty("主键ID")
    private Long recordId;

    @ApiModelProperty(value = "参考数据ID")
    @NotNull
    private Long dataId;

    @ApiModelProperty(value = "工作流类型 1-发布 0-下线")
    @NotNull
    private Integer recordType;

    @ApiModelProperty(value = "工作流状态 0-进行中 1-通过 2-拒绝")
    @NotNull
    private Integer recordStatus;

    @ApiModelProperty(value = "工作流实例ID")
    @NotNull
    private Long instanceId;

    @ApiModelProperty(value = "申请用户ID")
    @NotNull
    private Long applyUserId;

    @ApiModelProperty(value = "项目ID")
    @NotNull
    private Long projectId;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

}
