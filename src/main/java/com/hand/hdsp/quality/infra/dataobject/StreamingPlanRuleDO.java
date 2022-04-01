package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>实时数据方案-规则表数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StreamingPlanRuleDO extends AuditDomain {

    private Long planRuleId;

    private Long planBaseId;

    private String ruleCode;

    private String ruleName;

    private String ruleDesc;

    private String ruleType;

    private Long weight;

    private Long warningCount;

    private Long tenantId;

    private Long projectId;
}
