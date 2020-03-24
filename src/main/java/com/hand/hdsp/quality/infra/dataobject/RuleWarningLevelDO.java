package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

/**
 * <p>规则告警等级表数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RuleWarningLevelDO extends AuditDomain {

    private Long levelId;

    private Long ruleLineId;

    private String warningLevel;

    private BigDecimal startValue;

    private BigDecimal endValue;

    private String unit;

    private Long tenantId;

}
