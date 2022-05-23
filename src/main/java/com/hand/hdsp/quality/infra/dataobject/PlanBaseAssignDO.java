package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>质检项分配表数据对象</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-20 15:36:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PlanBaseAssignDO extends AuditDomain {

    private Long baseAssignId;

    private Long planBaseId;

    private Long planId;

    private Long tenantId;

    private String datasourceCode;

    private Long projectId;

}
