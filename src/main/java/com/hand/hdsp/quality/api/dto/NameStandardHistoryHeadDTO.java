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
 * <p>命名标准执行历史头表 数据传输对象</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("命名标准执行历史头表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NameStandardHistoryHeadDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long historyHeadId;

    @ApiModelProperty(value = "命名标准ID，关联表XSTA_NAME_STANDARD")
    @NotNull
    private Long nameStandardId;

    @ApiModelProperty(value = "稽核总数")
    @NotNull
    private Long checkedNum;

    @ApiModelProperty(value = "异常数据数量")
    @NotNull
    private Long abnormalNum;

    @ApiModelProperty(value = "执行开始时间")
    @NotNull
    private Date execStartTime;

    @ApiModelProperty(value = "执行结束时间")
    @NotNull
    private Date execEndTime;

    @ApiModelProperty(value = "稽核状态 值集 HDSP.XSTA.NAME_STANDARD_CKECKED_STATUS")
    @NotNull
    private String checkedStatus;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

}
