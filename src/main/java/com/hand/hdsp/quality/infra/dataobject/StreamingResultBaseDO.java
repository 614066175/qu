package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>实时数据方案结果表-基础信息数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StreamingResultBaseDO extends AuditDomain {

    private Long resultBaseId;

    private Long resultId;

    private String connectorInfo;

    private String topicInfo;

    private Long ruleCount;

    private Long exceptionRuleCount;

    private Long tenantId;

    private Long projectId;

}
