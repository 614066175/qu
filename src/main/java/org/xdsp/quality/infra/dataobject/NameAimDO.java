package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>命名落标表数据对象</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NameAimDO extends AuditDomain {

    private Long aimId;

    private Long standardId;

    private String datasourceCode;

    private String datasourceType;

    private String excludeRule;

    private String excludeDesc;

    private Integer enabledFlag;

    private Long tenantId;

    private Long projectId;
}
