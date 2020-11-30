package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>标准落标关系表数据对象</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-30 10:23:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StanardAimRelationDO extends AuditDomain {

    private Long relationId;

    private Long aimId;

    private String aimType;

    private Long planId;

    private Long planBaseId;

    private Long planRuleId;

    private Long tenantId;

}
