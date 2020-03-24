package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>批数据方案-字段规则表数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BatchPlanFieldDO extends AuditDomain {

    private Long planFieldId;

    private Long planBaseId;

    private String fieldName;

    private String fieldDesc;

    private String ruleCode;

    private String ruleName;

    private String ruleDesc;

    private String checkType;

    private String ruleType;

    private Integer exceptionBlock;

    private Long weight;

    private Long tenantId;

}
