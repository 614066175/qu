package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>实时数据方案结果表数据对象</p>
 *
 * @author feng.liu01@hand-china.com 2020-03-24 16:19:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StreamingResultDO extends AuditDomain {

    private Long resultId;

    private Long planId;

    private BigDecimal mark;

    private Date startDate;

    private Date endDate;

    private String planStatus;

    private Date nextCountDate;

    private Long tenantId;

    private Long projectId;

}
