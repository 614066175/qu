package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>标准-标准组关系表数据对象</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-09 15:50:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StandardRelationDO extends AuditDomain {

    private Long relationId;

    private Long fieldStandardId;

    private Long standardTeamId;

    private Long tenantId;

    private Long projectId;

}
