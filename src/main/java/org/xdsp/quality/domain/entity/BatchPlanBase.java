package org.xdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>批数据方案-基础配置表实体</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants(prefix = "FIELD_")
@VersionAudit
@ModifyAudit
@Table(name = "xqua_batch_plan_base")
public class BatchPlanBase extends AuditDomain {

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long planBaseId;

    private String planBaseCode;

    //质检项名称
    private String planBaseName;

    private Long planId;

    private String datasourceType;

    private Long datasourceId;

    private String datasourceCode;

    private String datasourceSchema;

    private String sqlType;

    private String objectName;

    private String description;

    private String incrementStrategy;

    private String incrementColumn;

    private String whereCondition;

    private Long tenantId;

    private Long projectId;

    private Integer buildRuleFlag;

}
