package com.hand.hdsp.quality.domain.entity;

import com.hand.hdsp.quality.infra.constant.PlanConstant;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.hzero.boot.platform.lov.annotation.LovValue;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 规则校验项表实体
 *
 * @author wuzhong26857
 * @version 1.0
 * @date 2020/3/23 11:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants(prefix = "FIELD_")
@VersionAudit
@ModifyAudit
@Table(name = "xqua_rule_line")
public class RuleLine extends AuditDomain {

    @Id
    @GeneratedValue
    private Long ruleLineId;

    private Long ruleId;

    @LovValue(lovCode = "HDSP.XQUA.CHECK_WAY", meaningField = "checkWayMeaning")
    private String checkWay;

    @LovValue(lovCode = PlanConstant.LOV_CHECK_ITEM, meaningField = "checkItemMeaning")
    private String checkItem;

    @LovValue(lovCode = PlanConstant.LOV_COUNT_TYPE, meaningField = "countTypeMeaning")
    private String countType;

    @LovValue(lovCode = "HDSP.XQUA.COMPARE_WAY", meaningField = "compareWayMeaning")
    private String compareWay;

    private String regularExpression;

    private String warningLevel;

    private Long tenantId;

    private Long projectId;
}
