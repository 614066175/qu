package org.xdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>命名落标表实体</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xsta_name_aim")
public class NameAim extends AuditDomain {

    public static final String FIELD_AIM_ID = "aimId";
    public static final String FIELD_STANDARD_ID = "standardId";
    public static final String FIELD_DATASOURCE_CODE = "datasourceCode";
    public static final String FIELD_DATASOURCE_TYPE = "datasourceType";
    public static final String FIELD_EXCLUDE_RULE = "excludeRule";
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
    private Long aimId;

    private Long standardId;

    private String datasourceCode;

    private String datasourceType;

    private String excludeRule;

    private String excludeDesc;

    private Integer enabledFlag;

    private Long tenantId;

    private Long projectId;

}