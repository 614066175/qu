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
 * <p>字段标准匹配记录表 数据传输对象</p>
 *
 * @author shijie.gao@hand-china.com 2022-12-07 10:42:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("字段标准匹配记录表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RootMatchHisDTO extends AuditDomain {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty(value = "批次号")
    @NotBlank
    @Size(max = 120)
    private String batchNumber;

    @ApiModelProperty(value = "数据数量")
    private Long dataCount;

    @ApiModelProperty(value = "当前状态")
    @NotBlank
    @Size(max = 30)
    private String status;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "租户id")
    private Long tenantId;

}
