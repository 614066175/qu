package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
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

    private String countType;

    private String compareWay;

    private String regularExpression;

    private String warningLevel;

    private Long tenantId;

    private Long projectId;

}
