package org.xdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>标准落标表实体</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-30 10:23:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xsta_standard_aim")
public class StandardAim extends AuditDomain {

    public static final String FIELD_AIM_ID = "aimId";
    public static final String FIELD_STANDARD_ID = "standardId";
    public static final String FIELD_STANDARD_TYPE = "standardType";
    public static final String FIELD_FIELD_NAME = "fieldName";
    public static final String FIELD_FIELD_DESC = "fieldDesc";
    public static final String FIELD_DATASOURCE_ID = "datasourceId";
    public static final String FIELD_DATASOURCE_CODE = "datasourceCode";
    public static final String FIELD_DATASOURCE_TYPE = "datasourceType";
    public static final String FIELD_SCHEMA_NAME = "schemaName";
    public static final String FIELD_TABLE_NAME = "tableName";
    public static final String FIELD_TABLE_DESC = "tableDesc";
    public static final String FIELD_PLAN_ID="planId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_PROJECT_ID = "projectId";

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

    private String standardType;

    private String fieldName;

    private String fieldDesc;

    private Long datasourceId;

    private String datasourceCode;

    private String datasourceType;

    private String schemaName;

    private String tableName;

    private String tableDesc;

    private Long planId;

    private Long tenantId;

    private Long projectId;

}