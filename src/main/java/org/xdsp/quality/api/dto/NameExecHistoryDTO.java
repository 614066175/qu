package org.xdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hzero.boot.platform.lov.annotation.LovValue;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

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

    @LovValue(lovCode = "XSTA.EXEC_STATUS",meaningField = "execStatusMeaning")
    @ApiModelProperty(value = "稽核状态 (快码：XSTA.EXEC_STATUS) <READY:就绪 RUNNING：执行中 SUCCESS：成功 FAILED：失败>")
    @NotBlank
    @Size(max = 30)
    private String execStatus;

    @ApiModelProperty(value = "错误详情")
    private String errorMessage;

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

    private Long projectId;
}
