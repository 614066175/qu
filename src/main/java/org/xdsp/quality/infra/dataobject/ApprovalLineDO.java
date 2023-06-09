package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>申请行表数据对象</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-25 20:35:06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ApprovalLineDO extends AuditDomain {

    private Long approvalLineId;

    private Long approvalId;

    private String operation;

    private Long tenantId;

    private Long projectId;
}
