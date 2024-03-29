package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>命名标准排除规则表数据对象</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NameAimRuleDO extends AuditDomain {

    private Long ruleId;

    private Long aimId;

    private String excludeRule;

    private Integer enabledFlag;

    private Long tenantId;

    private Long projectId;

}
