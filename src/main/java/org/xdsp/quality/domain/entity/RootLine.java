package org.xdsp.quality.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 词根中文名行表
 *
 * @author xin.sheng01@china-hand.com 2022-11-24 10:32:33
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("词根中文名行表")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "xsta_root_line")
public class RootLine extends AuditDomain {

    public static final String FIELD_ROOT_LINE_ID = "rootLineId";
    public static final String FIELD_ROOT_ID = "rootId";
    public static final String FIELD_ROOT_NAME = "rootName";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_PROJECT_ID = "projectId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("")
    @Id
    @GeneratedValue
    private Long rootLineId;
    @ApiModelProperty(value = "root主键",required = true)
    @NotNull
    private Long rootId;
    @ApiModelProperty(value = "词根中文名",required = true)
    @NotBlank
    private String rootName;
    @ApiModelProperty(value = "租户ID",required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "项目ID",required = true)
    @NotNull
    private Long projectId;
}
