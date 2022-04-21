package com.hand.hdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>LOV表实体</p>
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
@Table(name = "xqua_lov_version")
public class LovVersion extends AuditDomain {

    public static final String FIELD_LOV_VERSION_ID = "lovVersionId";
    public static final String FIELD_LOV_ID = "lovId";
    public static final String FIELD_LOV_CODE = "lovCode";
    public static final String FIELD_LOV_TYPE_CODE = "lovTypeCode";
    public static final String FIELD_ROUTE_NAME = "routeName";
    public static final String FIELD_LOV_NAME = "lovName";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_PARENT_LOV_CODE = "parentLovCode";
    public static final String FIELD_PARENT_TENANT_ID = "parentTenantId";
    public static final String FIELD_CUSTOM_SQL = "customSql";
    public static final String FIELD_CUSTOM_URL = "customUrl";
    public static final String FIELD_VALUE_FIELD = "valueField";
    public static final String FIELD_DISPLAY_FIELD = "displayField";
    public static final String FIELD_MUST_PAGE_FLAG = "mustPageFlag";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_TRANSLATION_SQL = "translationSql";
    public static final String FIELD_PUBLIC_FLAG = "publicFlag";
    public static final String FIELD_ENCRYPT_FIELD = "encryptField";
    public static final String FIELD_DECRYPT_FIELD = "decryptField";
    public static final String FIELD_REQUEST_METHOD = "requestMethod";
    public static final String FIELD_LOV_VERSION = "lovVersion";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long lovVersionId;

    private Long lovId;

    private String lovCode;

    private String lovTypeCode;

    private String routeName;

    private String lovName;

    private String description;

    private Long tenantId;

    private String parentLovCode;

    private Long parentTenantId;

    private String customSql;

    private String customUrl;

    private String valueField;

    private String displayField;

    private Integer mustPageFlag;

    private Integer enabledFlag;

    private String translationSql;

    private Integer publicFlag;

    private String encryptField;

    private String decryptField;

    private String requestMethod;

    private Long lovVersion;


}