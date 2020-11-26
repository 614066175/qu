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
 * <p>命名标准执行历史行表 数据传输对象</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("命名标准执行历史行表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NameStandardHistoryLineDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long historyLineId;

    @ApiModelProperty(value = "命名标准执行历史头ID，关联表XSTA_NAME_STANDARD_HISTORY_HEAD")
    @NotNull
    private Long historyHeadId;

    @ApiModelProperty(value = "数据库编码")
    @NotBlank
    @Size(max = 255)
    private String databaseCode;

    @ApiModelProperty(value = "表编码")
    @NotBlank
    @Size(max = 255)
    private String tableCode;

    @ApiModelProperty(value = "是否正常 1-正常 0-异常")
    @NotBlank
    @Size(max = 255)
    private String normalFlag;

    @ApiModelProperty(value = "资源路径")
    private String sourcePath;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

}
