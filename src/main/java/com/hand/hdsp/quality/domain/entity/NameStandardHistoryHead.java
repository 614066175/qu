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
 * <p>命名标准执行历史头表实体</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@VersionAudit
@ModifyAudit
@Table(name = "xsta_name_standard_history_head")
public class NameStandardHistoryHead extends AuditDomain {

    public static final String FIELD_HISTORY_HEAD_ID = "historyHeadId";
    public static final String FIELD_NAME_STANDART_ID = "nameStandartId";
    public static final String FIELD_CHECKED_NUM = "checkedNum";
    public static final String FIELD_ABNORMAL_NUM = "abnormalNum";
    public static final String FIELD_EXEC_START_TIME = "execStartTime";
    public static final String FIELD_EXEC_END_TIME = "execEndTime";
    public static final String FIELD_CHECKED_STATUS = "checkedStatus";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    private Long historyHeadId;

    private Long nameStandartId;

    private Long checkedNum;

    private Long abnormalNum;

    private Date execStartTime;

    private Date execEndTime;

    private Integer checkedStatus;

    private Long tenantId;


}