package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>数据对象</p>
 *
 * @author guoliang.li01@hand-china.com 2022-04-20 16:28:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class WorkOrderOperationDO extends AuditDomain {

    private Long operateId;

    private String workOrderId;

    private Long operaterId;

    private Long operateType;

    private String processComment;

    private Long tenantId;

    private Long projectId;

}
