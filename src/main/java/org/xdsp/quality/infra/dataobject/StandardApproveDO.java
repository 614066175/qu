package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>标准申请记录表数据对象</p>
 *
 * @author guoliangli01.@hand-china.com 2020-11-25 17:03:12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StandardApproveDO extends AuditDomain {

    private Long approveId;

    private String standardName;

    private String standardDesc;

    private String standardType;

    private String operation;

    private Long applyId;

    private Long tenantId;

    private Long projectId;
}
