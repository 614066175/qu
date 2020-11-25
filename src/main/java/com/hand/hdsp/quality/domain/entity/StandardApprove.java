package com.hand.hdsp.quality.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>标准申请记录表实体</p>
 *
 * @author guoliangli01.@hand-china.com 2020-11-25 17:03:12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xsta_standard_approve")
public class StandardApprove extends AuditDomain {

    public static final String FIELD_APPROVE_ID = "approveId";
    public static final String FIELD_STANDARD_NAME = "standardName";
    public static final String FIELD_STANDARD_DESC = "standardDesc";
    public static final String FIELD_STANDARD_TYPE = "standardType";
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
    private Long approveId;

    private String standardName;

    private String standardDesc;

    private String standardType;

    private String operation;

    private Long applyId;

    private Long tenantId;


}