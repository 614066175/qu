package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>数据对象</p>
 *
 * @author guoliang.li01@hand-china.com 2022-09-26 14:04:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PlanShareDO extends AuditDomain {

    private Long shareId;

    private String shareObjectType;

    private Long shareObjectId;

    private Long shareFromProjectId;

    private Long shareToProjectId;

    private Long tenantId;

    private Long projectId;

}
