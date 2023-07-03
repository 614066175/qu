package org.xdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>实体</p>
 *
 * @author guoliang.li01@hand-china.com 2022-09-26 14:04:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xqua_plan_share")
public class PlanShare extends AuditDomain {

    public static final String FIELD_SHARE_ID = "shareId";
    public static final String FIELD_SHARE_OBJECT_TYPE = "shareObjectType";
    public static final String FIELD_SHARE_OBJECT_ID = "shareObjectId";
    public static final String FIELD_SHARE_FROM_PROJECT_ID = "shareFromProjectId";
    public static final String FIELD_SHARE_TO_PROJECT_ID = "shareToProjectId";
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
    private Long shareId;

    private String shareObjectType;

    private Long shareObjectId;

    private Long shareFromProjectId;

    private Long shareToProjectId;

    private Long tenantId;

    private Long projectId;


}