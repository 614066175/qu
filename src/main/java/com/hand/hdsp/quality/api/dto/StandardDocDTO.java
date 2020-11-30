package com.hand.hdsp.quality.api.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p>标准文档管理表 数据传输对象</p>
 *
 * @author hua.shi@hand-china.com 2020-11-24 17:50:14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("标准文档表")
@JsonInclude(JsonInclude.Include.NON_NULL)
@VersionAudit
@ModifyAudit
public class StandardDocDTO extends AuditDomain {

    @ApiModelProperty("标准ID")
    private Long docId;

    @ApiModelProperty(value = "分组ID")
    @NotNull
    private Long groupId;

    @ApiModelProperty(value = "数据标准编码")
    @NotBlank
    @Size(max = 50)
    private String standardCode;

    @ApiModelProperty(value = "数据标准名称")
    @NotBlank
    @Size(max = 120)
    private String standardName;

    @ApiModelProperty(value = "数据标准描述")
    private String standardDesc;

    @ApiModelProperty(value = "数据标准文档名称")
    private String docName;

    @ApiModelProperty(value = "数据标准文档路径")
    private String docPath;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;
}
