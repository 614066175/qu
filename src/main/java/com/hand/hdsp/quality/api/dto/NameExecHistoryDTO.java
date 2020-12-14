package com.hand.hdsp.quality.api.dto;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hzero.boot.platform.lov.annotation.LovValue;

/**
 * <p>命名标准执行历史表 数据传输对象</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("命名标准执行历史表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NameExecHistoryDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long historyId;

    @ApiModelProperty(value = "命名标准ID，关联表XSTA_NAME_STANDARD")
    @NotNull
    private Long standardId;

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

    @ApiModelProperty(value = "校验规则")
    @NotBlank
    private String execRule;

    @LovValue(lovCode = "HDSP.XSTA.EXEC_STATUS",meaningField = "execStatusMeaning")
    @ApiModelProperty(value = "稽核状态 (快码：HDSP.XSTA.EXEC_STATUS) <READY:就绪 RUNNING：执行中 SUCCESS：成功 FAILED：失败>")
    @NotBlank
    @Size(max = 30)
    private String execStatus;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    //===============================================================================
    //  说明：业务字段
    //===============================================================================

    /**
     * 执行状态值集意义
     */
    @Transient
    private String execStatusMeaning;

    @Transient
    private List<NameExecHisDetailDTO> execDetails;

    @Transient
    private Date startDay;

}
