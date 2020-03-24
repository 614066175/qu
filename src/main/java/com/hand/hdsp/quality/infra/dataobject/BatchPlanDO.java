package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>批数据评估方案表数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BatchPlanDO extends AuditDomain {

    private Long planId;

    private Long groupId;

    private String planCode;

    private String planName;

    private String planDesc;

    private String warningCode;

    private Long tenantId;

}
