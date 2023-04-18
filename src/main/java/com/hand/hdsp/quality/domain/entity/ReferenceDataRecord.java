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
 * <p>参考数据工作流记录表实体</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-17 20:01:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xqua_reference_data_record")
public class ReferenceDataRecord extends AuditDomain {

    public static final String FIELD_RECORD_ID = "recordId";
    public static final String FIELD_DATA_ID = "dataId";
    public static final String FIELD_RECORD_TYPE = "recordType";
    public static final String FIELD_RECORD_STATUS = "recordStatus";
    public static final String FIELD_INSTANCE_ID = "instanceId";
    public static final String FIELD_APPLY_USER_ID = "applyUserId";
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
    private Long recordId;

    private Long dataId;

    private Integer recordType;

    private Integer recordStatus;

    private Long instanceId;

    private Long applyUserId;

    private Long projectId;

    private Long tenantId;


}