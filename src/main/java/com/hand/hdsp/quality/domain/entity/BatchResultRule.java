package com.hand.hdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * <p>批数据方案结果表-规则信息实体</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants(prefix = "FIELD_")
@VersionAudit
@ModifyAudit
@Table(name = "xqua_batch_result_rule")
public class BatchResultRule extends AuditDomain {

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long resultRuleId;

    private Long resultBaseId;

    private String ruleType;

    private String tableName;

    private Long ruleId;

    private String ruleCode;

    private String ruleName;

    private String checkItem;

    private String expectedValue;

    private String actualValue;

    private BigDecimal waveRate;

    private String exceptionInfo;

    private String warningLevel;

    private String fieldName;

    private String relTableName;

    private Long tenantId;


}
