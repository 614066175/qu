package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

/**
 * <p>命名标准执行历史表数据对象</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NameExecHistoryDO extends AuditDomain {

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

    private Long projectId;

}
