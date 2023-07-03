package org.xdsp.quality.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>实体</p>
 *
 * @author guoliang.li01@hand-china.com 2022-04-20 16:28:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xqua_work_order")
public class WorkOrder extends AuditDomain {

    public static final String FIELD_WORK_ORDER_ID = "workOrderId";
    public static final String FIELD_WORK_ORDER_CODE = "workOrderCode";
    public static final String FIELD_PLAN_ID = "planId";
    public static final String FIELD_RESULT_ID = "resultId";
    public static final String FIELD_PROCESSORS_ID = "processorsId";
    public static final String FIELD_IMMEDIATE_LEVEL = "immediateLevel";
    public static final String FIELD_ORDER_DESC = "orderDesc";
    public static final String FIELD_WORK_ORDER_STATUS = "workOrderStatus";
    public static final String FIELD_RELATE_JOB_TYPE = "relateJobType";
    public static final String FIELD_RELATE_JOB_ID = "relateJobId";
    public static final String FIELD_ORDER_SOLUTION = "orderSolution";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_PROJECT_ID = "projectId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
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