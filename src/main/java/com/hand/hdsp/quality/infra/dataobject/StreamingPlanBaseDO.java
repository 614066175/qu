package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>实时数据方案-基础配置表数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StreamingPlanBaseDO extends AuditDomain {

    private Long planBaseId;

    private Long planId;

    private String connectorInfo;

    private String topicInfo;

    private String planDesc;

    private Long tenantId;

    private Long projectId;

}
