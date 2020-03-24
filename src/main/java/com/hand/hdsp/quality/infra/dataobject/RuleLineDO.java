package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

/**
 * <p>规则校验项表数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 09:31:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RuleLineDO extends AuditDomain {

    private Long ruleLineId;

    private Long ruleId;

    private String checkWay;

    private String checkItem;

    private String compareWay;

    private String expectedValue;

    private String regularExpression;

    private Long tenantId;

}
