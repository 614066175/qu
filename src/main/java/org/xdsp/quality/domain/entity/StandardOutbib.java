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
 * @author guoliangli01@hand-china.com 2020-11-25 17:44:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xsta_standard_outbib")
public class StandardOutbib extends AuditDomain {

    public static final String FIELD_OUTBIB_ID = "outbibId";
    public static final String FIELD_STANDARD_ID = "standardId";
    public static final String FIELD_STANDARD_TYPE = "standardType";
    public static final String FIELD_FIELD_NAME = "fieldName";
    public static final String FIELD_FIELD_DESC = "fieldDesc";
    public static final String FIELD_DATASOURCE_CODE = "datasourceCode";
    public static final String FIELD_DATASOURCE_TYPE = "datasourceType";
    public static final String FIELD_DATASOURCE_CATALOG = "datasourceCatalog";
    public static final String FIELD_DATASOURCE_SCHEMA = "datasourceSchema";
    public static final String FIELD_TABLE_NAME = "tableName";
    public static final String FIELD_TABLE_DESC = "tableDesc";
    public static final String FIELD_PLAN_ID = "planId";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long outbibId;

    private Long standardId;

    private String standardType;

    private String fieldName;

    private String fieldDesc;

    private String datasourceCode;

    private String datasourceType;

    private String datasourceCatalog;

    private String datasourceSchema;

    private String tableName;

    private String tableDesc;

    private Long planId;

    private Long tenantId;

    private Long projectId;

}