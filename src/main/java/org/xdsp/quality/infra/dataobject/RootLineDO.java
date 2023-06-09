package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>词根中文名行表数据对象</p>
 *
 * @author xin.sheng01@china-hand.com 2022-11-24 11:48:12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RootLineDO extends AuditDomain {

    private Long rootLineId;

    private Long rootId;

    private String rootName;

    private Long tenantId;

    private Long projectId;

}
