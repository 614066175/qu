package org.xdsp.quality.api.dto;

import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>任务</p>
 *
 * @author xiezixi
 * @version 1.0
 * @date 2018/11/28 上午9:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("任务表")
public class JobDTO extends AuditDomain {

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @ApiModelProperty("表id，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long jobId;
    @ApiModelProperty(value = "任务名称")
    @NotBlank
    private String jobName;
    @ApiModelProperty(value = "任务描述")
    private String jobDescription;
    @ApiModelProperty(value = "任务分类")
    private String jobClass;
    @ApiModelProperty(value = "任务类型")
    @NotBlank
    private String jobType;
    @ApiModelProperty(value = "任务文件路径")
    private String jobPath;
    @ApiModelProperty(value = "命令")
    private String jobCommand;
    @ApiModelProperty(value = "是否启用")
    @NotNull
    private Integer enabledFlag;
    @ApiModelProperty(value = "关联任务主题表")
    @NotNull
    private Long themeId;
    @ApiModelProperty(value = "关联任务主题层次表")
    @NotNull
    private Long layerId;
    @ApiModelProperty(value = "租户id")
    private Long tenantId;
    @ApiModelProperty(value = "来源任务ID")
    private Long sourceJobId;
    @NotBlank
    @ApiModelProperty(value = "任务定义等级")
    private String jobLevel;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;
    // 非数据库字段

    private String themeName;
    private String layerName;
    private String themeDescription;
    @Transient
    private Long workflowId;

    private Boolean failed;

    private String code;

    private String message;

    private String type;

    private Long projectId;

}
