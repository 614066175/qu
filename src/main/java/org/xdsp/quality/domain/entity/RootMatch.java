package org.xdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>字段标准匹配表实体</p>
 *
 * @author shijie.gao@hand-china.com 2022-12-07 10:42:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xsta_root_match")
public class RootMatch extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_BATCH_NUMBER = "batchNumber";
    public static final String FIELD_FIELD_COMMENT = "fieldComment";
    public static final String FIELD_MATCHING_STATUS = "matchingStatus";
    public static final String FIELD_FIELD_NAME = "fieldName";
    public static final String FIELD_SOURCE = "source";
    public static final String FIELD_FIELD_ID = "fieldId";
    public static final String FIELD_PROJECT_ID = "projectId";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long id;

    private String batchNumber;

    private String fieldComment;

    private String matchingStatus;

    private String fieldName;

    private String source;

    private Long fieldId;

    private Long projectId;

    private Long tenantId;


}