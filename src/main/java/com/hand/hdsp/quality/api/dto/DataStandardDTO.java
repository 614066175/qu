package com.hand.hdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p>
 * description
 * </p>
 *
 * @author lgl 2020/11/24 14:02
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("数据标准表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataStandardDTO extends AuditDomain {

    @ApiModelProperty(value = "表主键")
    private Long standardId;

    @ApiModelProperty(value = "标准版本")
    private Long versionNumber;

    @ApiModelProperty(value = "分组Id")
    private Long groupId;

    @ApiModelProperty(value = "标准编码")
    private String standardCode;

    @ApiModelProperty(value = "标准名称")
    private String standardName;

    @ApiModelProperty(value = "标准描述")
    private String standardDesc;

    @ApiModelProperty(value = "数据类型")
    private String dataType;

    @ApiModelProperty(value = "数据格式")
    private String dataPattern;

    @ApiModelProperty(value = "长度类型")
    private String lengthType;

    @ApiModelProperty(value = "数据长度")
    private String dataLength;

    @ApiModelProperty(value = "值域类型")
    private String valueType;

    @ApiModelProperty(value = "值域值")
    private String valueRange;

    @ApiModelProperty(value = "标准依据")
    private String standardAccord;

    @ApiModelProperty(value = "标准来源")
    private String standardSource;

    @ApiModelProperty(value = "责任人部门ID")
    private Long chargeDeptId;

    @ApiModelProperty(value = "责任人ID")
    private Long chargeId;

    @ApiModelProperty(value = "责任认电话")
    private String chargeTel;

    @ApiModelProperty(value = "责任人邮箱")
    private String chargeEmail;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "租户ID")
    private Long tenantId;
}
