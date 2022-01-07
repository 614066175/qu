package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>批数据方案-表间规则表数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BatchPlanRelTableDO extends AuditDomain {

    private Long planRuleId;

    private Long planBaseId;

    private String ruleCode;

    private String ruleName;

    private String ruleDesc;

    private String checkType;

    private Integer exceptionBlock;

    private Long weight;

    private String relDatasourceType;

    private Long relDatasourceId;

    private String relSchema;

    private String relTableName;

    private String whereCondition;

    private String relationship;

    private String warningLevel;

    private Long tenantId;

    private Long projectId;
}
