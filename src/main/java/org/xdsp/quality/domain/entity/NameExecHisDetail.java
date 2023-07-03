package org.xdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>命名标准执行历史详情表实体</p>
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
@Table(name = "xsta_name_exec_his_detail")
public class NameExecHisDetail extends AuditDomain {

    public static final String FIELD_DETAIL_ID = "detailId";
    public static final String FIELD_HISTORY_ID = "historyId";
    public static final String FIELD_SCHEMA_NAME = "schemaName";
    public static final String FIELD_TABLE_NAME = "tableName";
    public static final String FIELD_ERROR_MESSAGE = "errorMessage";
    public static final String FIELD_SOURCE_PATH = "sourcePath";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long detailId;

    private Long historyId;

    private String schemaName;

    private String tableName;

    private String errorMessage;

    private String sourcePath;

    private Long tenantId;

    private Long projectId;

}