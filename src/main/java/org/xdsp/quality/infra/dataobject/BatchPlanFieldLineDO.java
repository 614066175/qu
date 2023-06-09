package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>批数据方案-字段规则校验项表数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BatchPlanFieldLineDO extends AuditDomain {

    private Long planLineId;

    private Long planRuleId;

    private String checkWay;

    private String checkItem;

    private String countType;

    private String fieldName;

    private String checkFieldName;

    private String regularExpression;

    private Long tenantId;

    private Long projectId;
}
