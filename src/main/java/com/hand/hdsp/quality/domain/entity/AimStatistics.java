package com.hand.hdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>标准落标统计表实体</p>
 *
 * @author guoliang.li01@hand-china.com 2022-06-16 14:38:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xsta_aim_statistics")
public class AimStatistics extends AuditDomain {

    public static final String FIELD_STATISTICS_ID = "statisticsId";
    public static final String FIELD_AIM_ID = "aimId";
    public static final String FIELD_ROW_NUM = "rowNum";
    public static final String FIELD_NON_NULL_ROW = "nonNullRow";
    public static final String FIELD_COMPLIANT_ROW = "compliantRow";
    public static final String FIELD_COMPLIANT_RATE = "compliantRate";
    public static final String FIELD_ACOMPLIANT_RATE = "acompliantRate";
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
    private Long statisticsId;

    private Long aimId;

    private Long rowNum;

    private Long nonNullRow;

    private Long compliantRow;

    private String compliantRate;

    private String acompliantRate;

    private Long tenantId;

    private Long projectId;


}