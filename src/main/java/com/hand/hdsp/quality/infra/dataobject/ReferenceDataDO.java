package com.hand.hdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.*;

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
public class ReferenceDataDO extends AuditDomain {

    private Long dataId;

    private String dataCode;

    private String dataName;

    private String dataDesc;

    private Long parentDataId;

    private Long dataGroupId;

    private String dataStatus;

    private Long releaseBy;

    private Date releaseDate;

    private Long responsibleDeptId;

    private Long responsiblePersonId;

    private String responsiblePersonTel;

    private String responsiblePersonEmail;

    private Long projectId;

    private Long tenantId;

}
