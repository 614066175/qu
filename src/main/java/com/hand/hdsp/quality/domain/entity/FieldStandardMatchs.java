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
 * <p>字段标准匹配记录表实体</p>
 *
 * @author SHIJIE.GAO@HAND-CHINA.COM 2022-11-22 17:55:57
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xsta_field_standard_matchs")
public class FieldStandardMatchs extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_BATCH_NUMBER = "batchNumber";
    public static final String FIELD_DATA_COUNT = "dataCount";
    public static final String FIELD_STATUS = "status";
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

    private Long dataCount;

    private String status;

    private Long projectId;

    private Long tenantId;


}