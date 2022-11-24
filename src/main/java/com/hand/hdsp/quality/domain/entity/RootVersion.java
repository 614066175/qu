package com.hand.hdsp.quality.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotBlank;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 词根版本
 *
 * @author xin.sheng01@china-hand.com 2022-11-21 14:28:19
 */
@Data
@Builder
@ApiModel("词根版本")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "xsta_root_version")
public class RootVersion extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_PROJECT_ID = "projectId";
    public static final String FIELD_ROOT_ID = "rootId";
    public static final String FIELD_ROOT_EN = "rootEn";
    public static final String FIELD_ROOT_EN_SHORT = "rootEnShort";
    public static final String FIELD_ROOT_NAME = "rootName";
    public static final String FIELD_ROOT_DESC = "rootDesc";
    public static final String FIELD_CHARGE_DEPT_ID = "chargeDeptId";
    public static final String FIELD_CHARGE_ID = "chargeId";
    public static final String FIELD_GROUP_ID = "groupId";
    public static final String FIELD_VERSION_NUMBER = "versionNumber";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("")
    @Id
    @GeneratedValue
    private Long id;
    @ApiModelProperty(value = "租户ID",required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "项目ID",required = true)
    @NotNull
    private Long projectId;
    @ApiModelProperty(value = "词根id",required = true)
    @NotNull
    private Long rootId;
   @ApiModelProperty(value = "词根英文全称")    
    private String rootEn;
    @ApiModelProperty(value = "词根英文简称",required = true)
    @NotBlank
    private String rootEnShort;
    @ApiModelProperty(value = "词根中文名，以、做分隔符",required = true)
    @NotBlank
    private String rootName;
   @ApiModelProperty(value = "词根描述")    
    private String rootDesc;
   @ApiModelProperty(value = "责任部门ID")    
    private Long chargeDeptId;
    @ApiModelProperty(value = "责任人ID",required = true)
    @NotNull
    private Long chargeId;
   @ApiModelProperty(value = "分组ID")    
    private Long groupId;
    @ApiModelProperty(value = "版本号",required = true)
    @NotNull
    private Long versionNumber;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

}
