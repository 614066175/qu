package com.hand.hdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>校验项模板SQL表实体</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-08 20:08:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xqua_item_template_sql")
public class ItemTemplateSql extends AuditDomain {

    public static final String FIELD_SQL_ID = "sqlId";
    public static final String FIELD_CHECK_ITEM = "checkItem";
    public static final String FIELD_DATASOURCE_TYPE = "datasourceType";
    public static final String FIELD_SQL_CONTENT = "sqlContent";
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
    private Long sqlId;

    private String checkItem;

    private String datasourceType;

    private String sqlContent;

    private Integer enabledFlag;

    private Long tenantId;


}
