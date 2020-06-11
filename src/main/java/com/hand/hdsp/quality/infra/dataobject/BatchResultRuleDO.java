package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

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

    private Long planRuleId;

    private String ruleCode;

    private String ruleName;

    private String ruleDesc;

    private String checkType;

    private Long weight;

    private Integer resultFlag;

    private Long tenantId;
}
