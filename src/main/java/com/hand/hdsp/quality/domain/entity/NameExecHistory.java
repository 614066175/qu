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
 * <p>命名标准执行历史表实体</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xsta_name_exec_history")
public class NameExecHistory extends AuditDomain {

    public static final String FIELD_HISTORY_ID = "historyId";
    public static final String FIELD_STANDARD_ID = "standardId";
    public static final String FIELD_CHECKED_NUM = "checkedNum";
    public static final String FIELD_ABNORMAL_NUM = "abnormalNum";
    public static final String FIELD_EXEC_START_TIME = "execStartTime";
    public static final String FIELD_EXEC_END_TIME = "execEndTime";
    public static final String FIELD_EXEC_STATUS = "execStatus";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_EXEC_RULE = "execRule";
    public static final String FIELD_ERROR_MESSAGE = "errorMessage";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long historyId;

    private Long standardId;

    private Long checkedNum;

    private Long abnormalNum;

    private Date execStartTime;

    private Date execEndTime;

    private String execRule;

    private String execStatus;

    private String errorMessage;

    private Long tenantId;


}