package com.hand.hdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * <p>规则告警等级表实体</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants(prefix = "FIELD_")
@VersionAudit
@ModifyAudit
@Table(name = "xqua_rule_warning_level")
public class RuleWarningLevel extends AuditDomain {

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long levelId;

    private Long ruleLineId;

    private String warningLevel;

    private BigDecimal startValue;

    private BigDecimal endValue;

    private String unit;

    private Long tenantId;


}
