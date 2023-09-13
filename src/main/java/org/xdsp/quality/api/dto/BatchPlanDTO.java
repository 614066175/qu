package org.xdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;
import org.xdsp.quality.domain.entity.BatchResult;
import org.xdsp.quality.domain.entity.BatchResultBase;
import org.xdsp.quality.domain.entity.PlanGroup;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * <p>批数据评估方案表 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("批数据评估方案表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@VersionAudit
@ModifyAudit
@ExcelSheet(zh = "评估方案", en = "batch plan")
public class BatchPlanDTO extends AuditDomain {

    //=========导出字段==========

    @ApiModelProperty(value = "方案编码")
    @NotBlank
    @Size(max = 50)
    @ExcelColumn(zh = "方案编码", en = "plan code", showInChildren = true)
    private String planCode;

    @ApiModelProperty(value = "方案名称")
    @NotBlank
    @Size(max = 255)
    @ExcelColumn(zh = "方案名称", en = "plan name")
    private String planName;

    @ApiModelProperty(value = "方案描述")
    @ExcelColumn(zh = "方案描述", en = "plan desc")
    private String planDesc;

    @ExcelColumn(zh = "评估方案基础配置", en = "batchPlanBaseDTOList", child = true)
    private List<BatchPlanBaseDTO> batchPlanBaseDTOList;


    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long planId;

    @ApiModelProperty(value = "评估方案分组表XQUA_PLAN_GROUP.GROUP_ID")
    @NotNull
    private Long groupId;

    @ApiModelProperty(value = "方案任务名称")
    @Size(max = 255)
    private String planJobCode;

    @ApiModelProperty(value = "告警发送代码")
    private String warningCode;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    // =========================非库字段=========================


    private BatchResult batchResult;

    private PlanGroup planGroup;

    private BatchResultBase batchResultBase;


    @Transient
    private String groupCode;

    @Transient
    private String planJobName;

    private Long projectId;

}
