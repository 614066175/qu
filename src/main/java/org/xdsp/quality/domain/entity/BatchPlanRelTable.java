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
 * <p>批数据方案-表间规则表实体</p>
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
@Table(name = "xqua_batch_plan_rel_table")
public class BatchPlanRelTable extends AuditDomain {

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long planRuleId;

    private Long planBaseId;

    private String ruleCode;

    private String ruleName;

    private String ruleDesc;

    private String checkType;

    private Integer exceptionBlock;

    private Long weight;

    private String checkItem;

    private String relDatasourceType;

    private Long relDatasourceId;

    private String relSchema;

    private String relTableName;

    private String whereCondition;

    private String relationship;

    private String tableRelCheck;

    private String warningLevel;

    private Long tenantId;

    private Long projectId;


}
