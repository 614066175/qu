package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import java.math.BigDecimal;

/**
 * <p>标准落标表数据对象</p>
 *
 * @author ZHENG.LI04@HAND-CHINA.COM 2022-05-17 15:20:56
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StandardStatisticsDO extends AuditDomain {

    private Long statisticsId;

    private Long rowNum;

    private Long aimId;

    private Long nonNullRow;

    private Long compliantRow;

    private String compliantRate;

    private String acompliantRate;

    private Long tenantId;

    private Long projectId;



}
