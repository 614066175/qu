package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import java.math.BigDecimal;

/**
 * <p>批数据方案结果表-规则信息数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BatchResultRuleDO extends AuditDomain {

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
