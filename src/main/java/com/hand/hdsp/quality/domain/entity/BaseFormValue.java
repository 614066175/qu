package com.hand.hdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>质检项表单值实体</p>
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
@Table(name = "xqua_base_form_value")
public class BaseFormValue extends AuditDomain {

    public static final String FIELD_RELATION_ID = "relationId";
    public static final String FIELD_PLAN_BASE_ID = "planBaseId";
    public static final String FIELD_FORM_LINE_ID = "formLineId";
    public static final String FIELD_FORM_VALUE = "formValue";
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

    private Long planBaseId;

    private Long formLineId;

    private String formValue;

    private Long tenantId;

    private Long projectId;


}