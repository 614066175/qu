package com.hand.hdsp.quality.api.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonInclude;

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
