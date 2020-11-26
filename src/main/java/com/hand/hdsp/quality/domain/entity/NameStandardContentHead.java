package com.hand.hdsp.quality.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

/**
 * <p>命名标准落标头表实体</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xsta_name_standard_content_head")
public class NameStandardContentHead extends AuditDomain {

    public static final String FIELD_CONTENT_HEAD_ID = "contentHeadId";
    public static final String FIELD_NAME_STANDARD_ID = "nameStandardId";
    public static final String FIELD_DATASOURCE_CODE = "datasourceCode";
    public static final String FIELD_DATASOURCE_TYPE = "datasourceType";
    public static final String FIELD_EXCLUDE_ROLE = "excludeRole";
    public static final String FIELD_EXCLUDE_DESC = "excludeDesc";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long contentHeadId;

    private Long nameStandardId;

    private String datasourceCode;

    private String datasourceType;

    private String excludeRole;

    private String excludeDesc;

    private Integer enabledFlag;

    private Long tenantId;


}