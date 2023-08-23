package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import java.util.Date;

/**
 * <p>各种标准审批表数据对象</p>
 *
 * @author fuqiang.luo@hand-china.com 2022-08-31 10:30:34
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StandardApprovalDO extends AuditDomain {

    private Long approvalId;

    private Long standardId;

    private String standardType;

    private Long applicantId;

    private String applyType;

    private Date applyTime;

    private Long approverId;

    private String approvalStatus;

    private Long instanceId;

    private Long tenantId;

}
