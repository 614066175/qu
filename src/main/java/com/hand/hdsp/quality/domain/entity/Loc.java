package com.hand.hdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>loc表实体</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-04-18 17:20:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xqua_loc")
public class Loc extends AuditDomain {

    public static final String FIELD_LOC_ID = "locId";
    public static final String FIELD_LOC_CODE = "locCode";
    public static final String FIELD_LOC_NAME = "locName";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_PARENT_LOC_CODE = "parentLocCode";
    public static final String FIELD_PARENT_TENANT_ID = "parentTenantId";
    public static final String FIELD_VALUE_FIELD = "valueField";
    public static final String FIELD_DISPLAY_FIELD = "displayField";
    public static final String FIELD_MUST_PAGE_FLAG = "mustPageFlag";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";


    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long locId;

    private String locCode;



    private String locName;

    private String description;

    private Long tenantId;

    private String parentLocCode;

    private Long parentTenantId;


    private String valueField;

    private String displayField;

    private Integer mustPageFlag;

    private Integer enabledFlag;




}