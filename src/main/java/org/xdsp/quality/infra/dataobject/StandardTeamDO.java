package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>标准表数据对象</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-09 15:50:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StandardTeamDO extends AuditDomain {

    private Long  standardTeamId;

    private String standardTeamCode;

    private String standardTeamName;

    private String standardTeamDesc;

    private Long parentTeamId;

    private Long inheriteTeamId;

    private Long tenantId;

    private Long projectId;

}
