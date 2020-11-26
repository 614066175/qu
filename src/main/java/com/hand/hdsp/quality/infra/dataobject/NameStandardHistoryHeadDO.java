package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

/**
 * <p>命名标准执行历史头表数据对象</p>
 *
 * @author 张鹏 2020-11-26 11:11:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NameStandardHistoryHeadDO extends AuditDomain {

    private Long historyHeadId;

    private Long nameStandartId;

    private Long checkedNum;

    private Long abnormalNum;

    private Date execStartTime;

    private Date execEndTime;

    private Integer checkedStatus;

    private Long tenantId;

}
