package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import java.math.BigDecimal;

/**
 * <p>方案告警等级表数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PlanWarningLevelDO extends AuditDomain {

    private Long levelId;

    private Long sourceId;

    private String sourceType;

    private String warningLevel;

    private BigDecimal startValue;

    private BigDecimal endValue;

    private String unit;

    private Long tenantId;

}
