package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>落标排除表数据对象</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NameAimExcludeDO extends AuditDomain {

    private Long excludeId;

    private Long aimId;

    private String schemaName;

    private String tableName;

    private Long tenantId;

    private Long projectId;

}
