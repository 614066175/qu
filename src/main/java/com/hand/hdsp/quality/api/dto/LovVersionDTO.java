package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * <p>LOV表 数据传输对象</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("LOV表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LovVersionDTO extends AuditDomain {

    @ApiModelProperty("主键，供其他表做外键")
    private Long lovVersionId;

    @ApiModelProperty(value = "")
    @NotNull
    private Long lovId;

    @ApiModelProperty(value = "LOV代码")
    @NotBlank
    @Size(max = 60)
    private String lovCode;

    @ApiModelProperty(value = "LOV数据类型: URL/SQL/FIXED")
    @NotBlank
    @Size(max = 30)
    private String lovTypeCode;

    @ApiModelProperty(value = "目标路由")
    private String routeName;

    @ApiModelProperty(value = "值集名称")
    @NotBlank
    @Size(max = 240)
    private String lovName;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "")
    private String parentLovCode;

    @ApiModelProperty(value = "父级值集租户ID")
    private Long parentTenantId;

    @ApiModelProperty(value = "自定义sql")
    private String customSql;

    @ApiModelProperty(value = "查询URL")
    private String customUrl;

    @ApiModelProperty(value = "值字段")
    private String valueField;

    @ApiModelProperty(value = "显示字段")
    private String displayField;

    @ApiModelProperty(value = "是否必须分页")
    @NotNull
    private Integer mustPageFlag;

    @ApiModelProperty(value = "是否启用")
    @NotNull
    private Integer enabledFlag;

    @ApiModelProperty(value = "翻译sql")
    private String translationSql;

    @ApiModelProperty(value = "是否公开权限，0:不公开 1:公开")
    @NotNull
    private Integer publicFlag;

    @ApiModelProperty(value = "加密字段")
    private String encryptField;

    @ApiModelProperty(value = "存储解密字段")
    private String decryptField;

    @ApiModelProperty(value = "请求方式，值集：HPFM.REQUEST_METHOD")
    @NotBlank
    @Size(max = 30)
    private String requestMethod;

    @ApiModelProperty(value = "任务版本号")
    private Long lovVersion;


    @Transient
    private String updaterName;
}
