package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>批数据方案-表级规则表数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BatchPlanTableDO extends AuditDomain {

    private Long planRuleId;

    private Long planBaseId;

    private String ruleCode;

    private String ruleName;

    private String ruleDesc;

    private String checkType;

    private Integer exceptionBlock;

    private Long weight;

    private String ruleType;

    private Long tenantId;

    private String checkItem;

    private Long projectId;
}
