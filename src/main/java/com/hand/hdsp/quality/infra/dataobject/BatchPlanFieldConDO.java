package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>批数据方案-字段规则条件表数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-06-03 20:03:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BatchPlanFieldConDO extends AuditDomain {

    private Long conditionId;

    private Long planLineId;

    private String whereCondition;

    private String compareWay;

    private String regularExpression;

    private String lovCode;

    private String warningLevel;

    private Long tenantId;

}
