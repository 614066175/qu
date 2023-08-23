package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>质检项表单值数据对象</p>
 *
 * @author guoliang.li01@hand-china.com 2022-05-20 15:36:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BaseFormValueDO extends AuditDomain {

    private Long relationId;

    private Long planBaseId;

    private Long formLineId;

    private String formValue;

    private Long tenantId;

    private String datasourceCode;

    private Long projectId;

}
