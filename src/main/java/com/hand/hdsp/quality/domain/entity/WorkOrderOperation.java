package com.hand.hdsp.quality.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.domain.AuditDomain;

import java.util.Date;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

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
@Table(name = "xqua_work_order_operation")
public class WorkOrderOperation extends AuditDomain {

    public static final String FIELD_ORDER_OPERATE_ID = "orderOperateId";
    public static final String FIELD_WORK_ORDER_ID = "workOrderId";
    public static final String FIELD_WORK_ORDER_STATUS = "workOrderStatus";
    public static final String FIELD_OPERATOR_ID = "operatorId";
    public static final String FIELD_OPERATE_TYPE = "operateType";
    public static final String FIELD_PROCESS_COMMENT = "processComment";
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
    private Long orderOperateId;

    private Long workOrderId;

    private String workOrderStatus;

    private Long operatorId;

    private String operateType;

    private String processComment;

    private Long tenantId;

    private Long projectId;


}