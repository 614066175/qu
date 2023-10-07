package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>附加信息版本表数据对象</p>
 *
 * @author guoliangli01@hand-china.com 2020-11-27 14:36:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ExtraVersionDO extends AuditDomain {

    private Long versionId;

    private Long standardId;

    private String standardType;

    private Long versionNumber;

    private String extraKey;

    private String extraValue;

    private Long tenantId;

    private Long projectId;

}
