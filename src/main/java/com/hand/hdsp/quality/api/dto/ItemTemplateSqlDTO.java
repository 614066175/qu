package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * <p>校验项模板SQL表 数据传输对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-08 20:08:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("校验项模板SQL表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemTemplateSqlDTO extends AuditDomain {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long sqlId;

    @ApiModelProperty(value = "校验项 HDSP.XQUA.CHECK_ITEM")
    private String checkItem;

    @ApiModelProperty(value = "数据源类型(快码：HDSP.DATASOURCE_TYPE)")
    private String datasourceType;

    @ApiModelProperty(value = "SQL")
    private String sqlContent;

    @ApiModelProperty(value = "标记")
    private String tag;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "是否启用 1-启用 0-不启用")
    @NotNull
    private Integer enabledFlag;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    private Long projectId;

}
