package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

import java.util.Date;

/**
 * <p>参考数据头表数据对象</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ReferenceDataHistoryDO extends AuditDomain {

    private Long historyId;

    private Long dataId;

    private String dataCode;

    private String dataName;

    private String dataDesc;

    private Long parentDataId;

    private Long dataGroupId;

    private String dataValueJson;

    private Long versionNumber;

    private Long releaseBy;

    private Date releaseData;

    private Long responsibleDeptId;

    private Long responsiblePersonId;

    private String responsiblePersonTel;

    private String responsiblePersonEmail;

    private Long projectId;

    private Long tenantId;

}
