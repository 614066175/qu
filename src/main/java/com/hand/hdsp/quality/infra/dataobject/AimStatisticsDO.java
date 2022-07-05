package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>标准落标统计表数据对象</p>
 *
 * @author guoliang.li01@hand-china.com 2022-06-16 14:38:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AimStatisticsDO extends AuditDomain {

    private Long statisticsId;

    private Long aimId;

    private Long rowNum;

    private Long nonNullRow;

    private Long compliantRow;

    private String compliantRate;

    private String acompliantRate;

    private Long tenantId;

    private Long projectId;

}
