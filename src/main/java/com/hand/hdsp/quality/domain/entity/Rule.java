package com.hand.hdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.mybatis.annotation.Unique;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 规则表实体
 *
 * @author wuzhong26857
 * @version 1.0
 * @date 2020/3/23 11:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants(prefix = "FIELD_")
@VersionAudit
@ModifyAudit
@Table(name = "xqua_rule")
public class Rule extends AuditDomain {

    @Id
    @GeneratedValue
    private Long ruleId;

    private String ruleModel;

    private Long GroupId;

    @Unique
    private String ruleCode;

    private String ruleName;

    private String ruleDesc;

    @LovValue(lovCode = "HDSP.XQUA.CHECK_TYPE", meaningField = "checkTypeMeaning")
    private String checkType;

    @LovValue(lovCode = "HDSP.XQUA.RULE_TYPE", meaningField = "ruleTypeMeaning")
    private String ruleType;

    private Integer exceptionBlock;

    private Long weight;

    private Integer enabledFlag;

    private Long tenantId;
}
