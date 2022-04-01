package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import java.util.Date;

/**
 * <p>实时数据方案结果表-规则信息数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StreamingResultRuleDO extends AuditDomain {

    private Long resultRuleId;

    private Long resultBaseId;

    private String ruleName;

    private String topicInfo;

    private String ruleType;

    private Date delayDate;

    private String exceptionInfo;

    private String warningLevel;

    private Long tenantId;

    private Long projectId;

}
