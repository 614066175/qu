package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * <p> 数据传输对象</p>
 *
 * @author abigballofmud 2020-04-26 16:32:12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestJobDTO extends AuditDomain {

    @ApiModelProperty("主键")
    private Long restJobId;

    @ApiModelProperty(value = "job名称")
    @NotBlank
    private String jobName;

    @ApiModelProperty(value = "是否是外部接口，默认外部接口")
    private Integer external;

    @ApiModelProperty(value = "若是内部接口，需配置服务名，如hdsp-factory")
    private String serviceCode;

    @ApiModelProperty(value = "若是内部接口，需配置服务简称，如xfac")
    private String serviceName;

    @ApiModelProperty("是否走网关")
    private Integer useGateway;

    @ApiModelProperty(value = "rest api url")
    @NotBlank
    private String url;

    @ApiModelProperty(value = "method，GET/POST/PUT/DELETE等等")
    private String method;

    @ApiModelProperty(value = "url后面的参数")
    private String query;

    @ApiModelProperty(value = "request body")
    private String body;

    @ApiModelProperty(value = "header，默认设置Content-Type=application/json")
    private String header;

    @ApiModelProperty(value = "额外配置（重试/异步回调等），json格式，方便扩展")
    private String settingInfo;

    @ApiModelProperty(value = "主题id")
    private Long themeId;

    @ApiModelProperty(value = "层次id")
    private Long layerId;

    @ApiModelProperty(value = "租户ID")
    private Long tenantId;

}
