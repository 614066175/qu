package org.xdsp.quality.infra.dataobject;

import io.choerodon.mybatis.domain.AuditDomain;
import lombok.*;

/**
 * <p>问题库表数据对象</p>
 *
 * @author lei.song@hand-china.com 2021-08-17 11:47:17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProblemDO extends AuditDomain {

    private Long problemId;

    private Long problemParentId;

    private String problemCode;

    private String problemName;

    private String problemType;

    private String problemDesc;

    private Long tenantId;

    private Long projectId;

}
