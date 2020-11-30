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
 * <p>命名标准执行历史详情表 数据传输对象</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("命名标准执行历史详情表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NameExecHisDetailDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long detailId;

    @ApiModelProperty(value = "命名标准执行历史ID，关联表XSTA_NAME_EXEC_HISTORY")
    @NotNull
    private Long historyId;

    @ApiModelProperty(value = "数据库名称")
    @NotBlank
    @Size(max = 120)
    private String schemaName;

    @ApiModelProperty(value = "排除的表名称")
    @NotBlank
    @Size(max = 120)
    private String tableName;

    @ApiModelProperty(value = "异常信息")
    private String errorMessage;

    @ApiModelProperty(value = "资源路径")
    private String sourcePath;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

}
