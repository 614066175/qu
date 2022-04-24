package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
public class WorkOrderDO extends AuditDomain {

    private Long workOrderId;

    private String workOrderCode;

    private Long planId;

    private Long resultId;

    private Long processorsId;

    private String immediateLevel;

    private String orderDesc;

    private String workOrderStatus;

    private String relateJobType;

    private Long relateJobId;

    private String orderSolution;

    private Long tenantId;

    private Long projectId;

}