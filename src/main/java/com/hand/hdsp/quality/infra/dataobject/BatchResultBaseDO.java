package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>批数据方案结果表-表信息数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BatchResultBaseDO extends AuditDomain {

    private Long resultBaseId;

    private Long resultId;

    private String tableName;

    private Long ruleCount;

    private Long exceptionRuleCount;

    private Long dataCount;

    private Long tenantId;

}
