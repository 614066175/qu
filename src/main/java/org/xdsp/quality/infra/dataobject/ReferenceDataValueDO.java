package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>参考数据值数据对象</p>
 *
 * @author fuqiang.luo@hand-china.com 2023-04-14 16:23:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ReferenceDataValueDO extends AuditDomain {

    private Long valueId;

    private Long dataId;

    private String value;

    private String valueMeaning;

    private Long valueSeq;

    private String valueDesc;

    private Long parentValueId;

    private Integer enabledFlage;

    private Long projectId;

    private Long tenantId;

}
