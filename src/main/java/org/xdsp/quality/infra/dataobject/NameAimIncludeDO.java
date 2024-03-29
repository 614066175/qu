package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>落标包含表数据对象</p>
 *
 * @author 张鹏 2020-11-27 16:35:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NameAimIncludeDO extends AuditDomain {

    private Long includeId;

    private Long aimId;

    private String schemaName;

    private Long tenantId;

    private Long projectId;

}
