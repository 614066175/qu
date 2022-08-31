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
 * <p>各种标准审批表实体</p>
 *
 * @author fuqiang.luo@hand-china.com 2022-08-31 10:30:34
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xsta_standard_approval")
public class StandardApproval extends AuditDomain {

    public static final String FIELD_APPROVAL_ID = "approvalId";
    public static final String FIELD_STANDARD_ID = "standardId";
    public static final String FIELD_STANDARD_TYPE = "standardType";
    public static final String FIELD_APPLICANT_ID = "applicantId";
    public static final String FIELD_APPLY_TYPE = "applyType";
    public static final String FIELD_APPLY_TIME = "applyTime";
    public static final String FIELD_APPROVER_ID = "approverId";
    public static final String FIELD_APPROVAL_STATUS = "approvalStatus";
    public static final String FIELD_INSTANCE_ID = "instanceId";
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

    private Long standardId;

    private String standardType;

    private Long applicantId;

    private String applyType;

    private Date applyTime;

    private Long approverId;

    private String approvalStatus;

    private Long instanceId;

    private Long tenantId;


}