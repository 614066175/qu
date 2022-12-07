package com.hand.hdsp.quality.api.dto;

import javax.persistence.Transient;
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
 * @author SHIJIE.GAO@HAND-CHINA.COM 2022-11-22 17:55:57
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("字段标准匹配记录表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldStandardMatchsDTO extends AuditDomain {

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

    /**
     * 最近更新时间查询 起
     */
    @Transient
    private String startDate;

    /**
     * 最近更新时间查询 止
     */
    @Transient
    private String endDate;

}
