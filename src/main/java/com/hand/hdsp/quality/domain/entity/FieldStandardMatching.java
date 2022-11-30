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
 * <p>字段标准匹配表实体</p>
 *
 * @author shijie.gao@hand-china.com 2022-11-30 10:31:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xsta_field_standard_matching")
public class FieldStandardMatching extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_BATCH_NUMBER = "batch_number";
    public static final String FIELD_FIELD_NAME = "fieldName";
    public static final String FIELD_FIELD_TYPE = "fieldType";
    public static final String FIELD_FIELD_COMMENT = "fieldComment";
    public static final String FIELD_FIELD_DESCRIPTION = "fieldDescription";
    public static final String FIELD_MATCHING_STATUS = "matchingStatus";
    public static final String FIELD_SOURCE = "source";
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

    private String fieldName;

    private String fieldType;

    private String fieldComment;

    private String fieldDescription;

    private String matchingStatus;

    private String source;

    private Long projectId;

    private Long tenantId;


}