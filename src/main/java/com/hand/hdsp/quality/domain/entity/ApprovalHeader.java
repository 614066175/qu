package com.hand.hdsp.quality.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>申请头表实体</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-25 20:35:06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xass_approval_header")
public class ApprovalHeader extends AuditDomain {

    public static final String FIELD_APPROVAL_ID = "approvalId";
    public static final String FIELD_RESOURCE_NAME = "resourceName";
    public static final String FIELD_RESOURCE_DESC = "resourceDesc";
    public static final String FIELD_ITEM_TYPE = "itemType";
    public static final String FIELD_OPERATION = "operation";
    public static final String FIELD_APPLY_ID = "applyId";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long approvalId;

    private String resourceName;

    private String resourceDesc;

    private String itemType;

    private String operation;

    private Long applyId;

    private Long tenantId;


}