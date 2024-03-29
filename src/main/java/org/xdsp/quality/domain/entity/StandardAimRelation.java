package org.xdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>标准落标关系表实体</p>
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
@Table(name = "xsta_standard_aim_relation")
public class StandardAimRelation extends AuditDomain {

    public static final String FIELD_RELATION_ID = "relationId";
    public static final String FIELD_AIM_ID = "aimId";
    public static final String FIELD_AIM_TYPE = "aimType";
    public static final String FIELD_PLAN_ID = "planId";
    public static final String FIELD_PLAN_BASE_ID = "planBaseId";
    public static final String FIELD_PLAN_RULE_ID = "planRuleId";
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
    private Long relationId;

    private Long aimId;

    private String aimType;

    private Long planId;

    private Long planBaseId;

    private Long planRuleId;

    private Long tenantId;

    private Long projectId;

}