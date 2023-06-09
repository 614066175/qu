package org.xdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>附加信息版本表实体</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xsta_extra_version")
public class ExtraVersion extends AuditDomain {

    public static final String FIELD_VERSION_ID = "versionId";
    public static final String FIELD_STANDARD_ID = "standardId";
    public static final String FIELD_STANDARD_TYPE = "standardType";
    public static final String FIELD_VERSION_NUMBER = "versionNumber";
    public static final String FIELD_EXTRA_KEY = "extraKey";
    public static final String FIELD_EXTRA_VALUE = "extraValue";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long versionId;

    private Long standardId;

    private String standardType;

    private Long versionNumber;

    private String extraKey;

    private String extraValue;

    private Long tenantId;

    private Long projectId;

}