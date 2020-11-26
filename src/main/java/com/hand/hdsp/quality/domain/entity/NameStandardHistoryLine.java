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
 * <p>命名标准执行历史行表实体</p>
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
@Table(name = "xsta_name_standard_history_line")
public class NameStandardHistoryLine extends AuditDomain {

    public static final String FIELD_HISTORY_LINE_ID = "historyLineId";
    public static final String FIELD_HISTORY_HEAD_ID = "historyHeadId";
    public static final String FIELD_DATABASE_CODE = "databaseCode";
    public static final String FIELD_TABLE_CODE = "tableCode";
    public static final String FIELD_NORMAL_FLAG = "normalFlag";
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
    private Long historyLineId;

    private Long historyHeadId;

    private String databaseCode;

    private String tableCode;

    private String normalFlag;

    private String sourcePath;

    private Long tenantId;


}