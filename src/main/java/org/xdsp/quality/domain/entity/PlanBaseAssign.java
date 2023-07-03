package org.xdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>质检项分配表实体</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-20 15:36:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xqua_plan_base_assign")
public class PlanBaseAssign extends AuditDomain {

    public static final String FIELD_BASE_ASSIGN_ID = "baseAssignId";
    public static final String FIELD_PLAN_BASE_ID = "planBaseId";
    public static final String FIELD_PLAN_ID = "planId";
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
    private Long baseAssignId;

    private Long planBaseId;

    private Long planId;

    private Long tenantId;

    private Long projectId;


}