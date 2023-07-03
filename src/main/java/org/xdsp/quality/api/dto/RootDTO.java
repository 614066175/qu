package org.xdsp.quality.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hzero.export.annotation.ExcelColumn;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * <p>词根 数据传输对象</p>
 *
 * @author xin.sheng01@china-hand.com 2022-11-24 11:48:12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("词根")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RootDTO extends AuditDomain {
    @ApiModelProperty("")
    private Long id;
    @ApiModelProperty(value = "租户ID",required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "项目ID",required = true)
    @NotNull
    private Long projectId;

    @ExcelColumn(zh = "词根英文简称", en = "rootEnShort", groups = {Group1.class})
    @ApiModelProperty(value = "词根英文简称",required = true)
    @NotBlank
    private String rootEnShort;

    @ExcelColumn(zh = "词根英文全称", en = "rootEn", groups = {Group1.class})
    @ApiModelProperty(value = "词根英文全称")
    private String rootEn;

    @ExcelColumn(zh = "词根描述", en = "rootDesc", groups = {Group1.class})
    @ApiModelProperty(value = "词根描述")
    private String rootDesc;
    @ApiModelProperty(value = "责任部门ID")
    private Long chargeDeptId;
    @ApiModelProperty(value = "责任人ID",required = true)
    @NotNull
    private Long chargeId;
    @ApiModelProperty(value = "状态(	XSTA.STANDARD_STATUS)")
    private String releaseStatus;
    @ApiModelProperty(value = "分组ID")
    private Long groupId;


    /**
     * 词根中文
     */
    @Transient
    @ExcelColumn(zh = "词根中文名", en = "rootName", groups = {Group1.class})
    private String rootName;

    /**
     * 责任人名
     */
    @ExcelColumn(zh = "责任人", en = "chargeName", groups = {Group2.class})
    @Transient
    private String chargeName;

    /**
     * 责任人电话
     */
    @ExcelColumn(zh = "责任人电话", en = "chargeTel", groups = {Group2.class})
    @Transient
    private String chargeTel;

    /**
     * 责任人电话
     */
    @ExcelColumn(zh = "责任人电话", en = "chargeEmail", groups = {Group2.class})
    @Transient
    private String chargeEmail;

    /**
     * 责任人部门
     */
    @ExcelColumn(zh = "责任部门", en = "chargeDept", groups = {Group2.class})
    @Transient
    private String chargeDept;

    /**
     * 最后更新人
     */
    @Transient
    private String lastUpdatedName;

    /**
     * 分组编码
     */
    @ExcelColumn(zh = "分组编码", en = "groupCode", groups = {Group1.class})
    @Transient
    private String groupCode;

    @Transient
    private String startUpdateDate;

    @Transient
    private String endUpdateDate;

    @Transient
    private String startCreateDate;

    @Transient
    private String endCreateDate;

    public interface Group1 {
    }

    public interface Group2 {
    }

    @ApiModelProperty("发布人id")
    private Long releaseBy;

    @Transient
    private String releaseByName;

    @ApiModelProperty("发布时间")
    private Date releaseDate;
}
