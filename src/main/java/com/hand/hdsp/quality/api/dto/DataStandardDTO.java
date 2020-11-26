package com.hand.hdsp.quality.api.dto;

import java.util.List;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p>数据标准表 数据传输对象</p>
 *
 * @author guoliangli01.@hand-china.com 2020-11-25 17:20:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("数据标准表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataStandardDTO extends AuditDomain {

    @ApiModelProperty("数据标准ID，主键，供其他表做外键")
    private Long standardId;

    @ApiModelProperty(value = "数据标准版本号")
    @NotNull
    private Long versionNumber;

    @ApiModelProperty(value = "分组ID")
    @NotNull
    private Long groupId;

    @ApiModelProperty(value = "数据标准编码")
    @NotBlank
    @Size(max = 50)
    private String standardCode;

    @ApiModelProperty(value = "数据标准名称")
    @NotBlank
    @Size(max = 255)
    private String standardName;

    @ApiModelProperty(value = "数据标准描述")
    private String standardDesc;

    @ApiModelProperty(value = "数据类型")
    @NotBlank
    @Size(max = 50)
    private String dataType;

    @ApiModelProperty(value = "数据格式")
    private String dataPattern;

    @ApiModelProperty(value = "长度类型")
    private String lengthType;

    @ApiModelProperty(value = "数据长度")
    private String dataLength;

    @ApiModelProperty(value = "值域类型")
    private String valueType;

    @ApiModelProperty(value = "值域")
    private String valueRange;

    @ApiModelProperty(value = "标准依据")
    private String standardAccord;

    @ApiModelProperty(value = "标准来源")
    private String standardSource;

    @ApiModelProperty(value = "责任部门ID")
    @NotNull
    private Long chargeDeptId;

    @ApiModelProperty(value = "责任人ID")
    @NotNull
    private Long chargeId;

    @ApiModelProperty(value = "责任人电话")
    private String chargeTel;

    @ApiModelProperty(value = "责任人邮箱")
    private String chargeEmail;

    @ApiModelProperty(value = "数据标准状态(新建，在线，离线，待审核)")
    @NotBlank
    @Size(max = 50)
    private String status;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @Transient
    private List<StandardExtraDTO> standardExtraDTOList;
}
