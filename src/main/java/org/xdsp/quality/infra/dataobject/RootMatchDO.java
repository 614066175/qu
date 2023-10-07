package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>字段标准匹配表数据对象</p>
 *
 * @author shijie.gao@hand-china.com 2022-12-07 10:42:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RootMatchDO extends AuditDomain {

    private Long id;

    private String batchNumber;

    private String fieldComment;

    private String matchingStatus;

    private String fieldName;

    private String source;

    private String fieldDescription;

    private String fieldType;

    private Long projectId;

    private Long tenantId;

}
