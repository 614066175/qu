package org.xdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * <p>数据标准版本表 数据传输对象</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:44
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("数据标准版本表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataStandardVersionDTO extends AuditDomain {

    @ApiModelProperty("版本ID，主键，供其他表做外键")
    private Long versionId;

    @ApiModelProperty(value = "分组ID")
    @NotNull
    private Long groupId;

    @ApiModelProperty(value = "标准ID")
    @NotNull
    private Long standardId;

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

    @ApiModelProperty(value = "数据类型(快码：XMOD.FIELD_TYPE)")
    @NotBlank
    @Size(max = 30)
    private String dataType;

    @ApiModelProperty(value = "数据格式")
    private String dataPattern;

    @ApiModelProperty(value = "长度类型（快码：HSDP.XSTA.LENGTH_TYPE）")
    private String lengthType;

    @ApiModelProperty(value = "数据长度")
    private String dataLength;

    @ApiModelProperty(value = "值域类型（快码：XSTA.VALUE_TYPE）")
    private String valueType;

    @ApiModelProperty(value = "值域")
    private String valueRange;

    @ApiModelProperty(value = "标准依据（快码：XSTA.STANDARD_ACCORD）")
    private String standardAccord;

    @ApiModelProperty(value = "依据内容")
    private String accordContent;

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

    @ApiModelProperty(value = "版本号")
    @NotNull
    private Long versionNumber;

    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

    @ApiModelProperty(value = "是否可为空，1可空 0 不可空")
    private Integer nullFlag;

    @ApiModelProperty(value = "业务用途")
    private String businessPurpose;

    @ApiModelProperty(value = "业务规则")
    private String businessRules;

    @ApiModelProperty(value = "数据示例")
    private String dataExample;

    @Transient
    private List<ExtraVersionDTO> extraVersionDTOList;

    @Transient
    private List<Long> dataLengthList;

    @Transient
    private String lastUpdateName;

    @Transient
    private String chargeName;

    @Transient
    private String chargeDeptName;

    @Transient
    private String groupName;

    @Transient
    private String groupPath;

    private Long projectId;

    @ApiModelProperty("发布人id")
    private Long releaseBy;

    @Transient
    private String releaseByName;

    @ApiModelProperty("发布时间")
    private Date releaseDate;
}
